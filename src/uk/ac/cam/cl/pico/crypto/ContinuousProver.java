/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

import uk.ac.cam.cl.pico.crypto.messages.EncPicoReauthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncServiceReauthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncryptedMessage.FieldDeserializationException;
import uk.ac.cam.cl.pico.crypto.messages.PicoReauthMessage;
import uk.ac.cam.cl.pico.crypto.messages.ReauthState;
import uk.ac.cam.cl.pico.crypto.messages.SequenceNumber;
import uk.ac.cam.cl.pico.crypto.messages.ServiceReauthMessage;
import uk.ac.cam.cl.pico.data.session.Session;

/**
 * When created with a session, the ContinuousAuthenticator reauthenticates
 * whenever the timer event is fired.
 * 
 * State machine
 * 
 * <pre>
 *                                      ╭─────╮
 *                         pause        │     │pause
 *           ╭─────╮  ╭───────────▶ Paused ◀──╯
 *  continue │     │  │              │  │
 *           ╰──▶ Active ◀───────────╯  │
 *                 │       continue     │
 *            stop │                    │
 *                 │  ╭─────────────────╯
 *                 │  │ stop              ┆ ┆ ┆
 *                 │  │                   │ │ │
 *                 ▼  ▼                   ▼ ▼ ▼
 *               Stopped                  Error
 * </pre>
 * 
 * NB: Error state can be entered from any state.
 * 
 * 
 * 
 * The continue and pause events move the state machine.
 * 
 * It simply allows the implementor to use the most natural timer construct
 * available to them.
 * 
 * Need to define what happens if pause, resume or stop events happen
 * unexpectedly.
 * 
 * The error event can only be triggered internally.
 * 
 * @author cw471
 * 
 */
final public class ContinuousProver implements Destroyable {

	public static enum State {
		ACTIVE, PAUSED, STOPPED, ERROR
	}

	public interface ProverStateChangeNotificationInterface {

		public void sessionPaused(final Session session);

		public void sessionContinued(final Session session);

		public void sessionStopped(final Session session);

		public void sessionError(final Session session);

		public void tick(final Session session);
	}

	public interface SchedulerInterface {

		/**
		 * The scheduler should call updateVerifier on the given prover within
		 * the time specified, disregarding the previously scheduled event.
		 * 
		 * @param milliseconds
		 * @param prover
		 */
		public void setTimer(int milliseconds, ContinuousProver prover);

		/**
		 * The scheduler should clear any scheduled calls to updateVerifier for
		 * the specified prover.
		 * 
		 * @param prover
		 */
		public void clearTimer(ContinuousProver prover);
	}

	private State state = State.ACTIVE;
	private final Session session;
	private final IContinuousVerifier serviceInterface;
	private final ProverStateChangeNotificationInterface proverStateChangeNotificationInterface;

	private final SchedulerInterface schedulerInterface;

	private SequenceNumber currentSequenceNumber;

	/**
	 * 
	 * @param session
	 *            The current session, including the secret key
	 * @param serviceInterface
	 *            The connection from the SigmaProver, may be a socket or an
	 *            HTTP route.
	 * @param proverStateChangeNotificationInterface
	 *            The application can be updated by callbacks by implementing
	 *            this interface.
	 * @param schedulerInterface
	 *            The implementor of the SchedulerInterface must call
	 *            prover.updateverifier() exactly once within the time specified
	 *            (i.e. it can be called earlier).
	 * @param currentSequenceNumber
	 */
	public ContinuousProver(Session session, IContinuousVerifier serviceInterface,
			ProverStateChangeNotificationInterface proverStateChangeNotificationInterface,
			SchedulerInterface schedulerInterface, SequenceNumber currentSequenceNumber) {
		this.serviceInterface = checkNotNull(serviceInterface);
		this.session = checkNotNull(session);
		this.proverStateChangeNotificationInterface = checkNotNull(proverStateChangeNotificationInterface);
		this.schedulerInterface = checkNotNull(schedulerInterface);
		this.currentSequenceNumber = checkNotNull(currentSequenceNumber);
	}

	/* ************************ State Entry Methods ************************ */

	/**
	 * Re-authenticate every time the timer() event is fired.
	 * 
	 * @return
	 */
	private void enterActive() {
		assert(isDestroyed == false);
		state = State.ACTIVE;
		updateVerifier();
		proverStateChangeNotificationInterface.sessionContinued(session);
	}

	private void enterPaused() {
		assert(isDestroyed == false);
		state = State.PAUSED;
		updateVerifier();
		proverStateChangeNotificationInterface.sessionPaused(session);
	}

	private void enterStopped() {
		assert(isDestroyed == false);
		state = State.STOPPED;
		updateVerifier();
		proverStateChangeNotificationInterface.sessionStopped(session);
	}

	private void enterError(Exception e) {
		assert(isDestroyed == false);
		state = State.ERROR;
		proverStateChangeNotificationInterface.sessionError(session);
	}

	/* ****************************** Signals ****************************** */

	public synchronized void updateVerifier() {

		if (isDestroyed == true)
			throw new IllegalStateException();

		if (state == State.ACTIVE || state == State.PAUSED || state == State.STOPPED) {
			try {
				// Generate response to the current sequenceNumber.
				ReauthState t;
				switch (this.state) {
				case ACTIVE:
					t = ReauthState.CONTINUE;
					break;
				case PAUSED:
					t = ReauthState.PAUSE;
					break;
				case STOPPED:
					t = ReauthState.STOP;
					break;
				default: // Never happens if no concurrent manipulation;
					throw new IllegalStateException();
				}
				final SequenceNumber c2 = currentSequenceNumber.getResponse();
				final PicoReauthMessage picoReauthMessage = new PicoReauthMessage(
						Integer.parseInt(session.getRemoteId()), t, c2);

				// Encrypt the reauth message
				EncPicoReauthMessage encPicoReauthMessage;
				try {
					encPicoReauthMessage = picoReauthMessage.encrypt(session.getSecretKey());
				} catch (InvalidKeyException e) {
					// Re-thrown unchecked because this signals incompatibility
					// between the session
					// encryption key and the chosen encryption cipher. This is
					// a configuration error
					// and is considered a fatal error from which the program
					// probably can't (and
					// shouldn't try to) recover.
					throw new CryptoRuntimeException(e);
				}

				// Send it and get the encrypted response
				final EncServiceReauthMessage encServiceReauthMessage = serviceInterface.reauth(encPicoReauthMessage);

				// Decrypt the response
				ServiceReauthMessage serviceReauthMessage;
				try {
					serviceReauthMessage = encServiceReauthMessage.decrypt(session.getSecretKey());
				} catch (InvalidKeyException e) {
					// Re-thrown unchecked because this signals incompatibility
					// between the session
					// encryption key and the chosen encryption cipher. This is
					// a configuration
					// error and is considered a fatal error from which the
					// program probably can't
					// (and shouldn't try to) recover.
					throw new CryptoRuntimeException(e);
				} catch (InvalidAlgorithmParameterException e) {
					// Any of the other exceptions caught here indicate that the
					// decryption failed due
					// to some error on the part of the prover.
					error();
					return;
				} catch (IllegalBlockSizeException e) {
					error();
					return;
				} catch (BadPaddingException e) {
					error();
					return;
				} catch (FieldDeserializationException e) {
					error();
					return;
				}

				// Verify the service's response
				final SequenceNumber c3 = serviceReauthMessage.getSequenceNumber();
				if (!c2.verifyResponse(c3)) {
					this.error();
					return;
				}

				final ReauthState newReauthState = serviceReauthMessage.getReauthState();

				switch (newReauthState) {
				case CONTINUE:
					resume();
					break;
				case PAUSE:
					pause();
					break;
				case STOP:
					stop();
					break;
				case ERROR:
					// Fall through
				default:
					error();
					break;

				}

				if (state == State.ACTIVE || state == State.PAUSED) {
					// Be ready to reply
					this.currentSequenceNumber = c3;

					this.schedulerInterface.setTimer(serviceReauthMessage.getTimeout(), this);
				} else {
					schedulerInterface.clearTimer(this);
				}

				proverStateChangeNotificationInterface.tick(session);
			} catch (IOException e) {
				this.error(e);
				return;
			}
		} // else do nothing
	}

	synchronized public void pause() {

		if (isDestroyed == true)
			throw new IllegalStateException();

		switch (state) {
		case ACTIVE:
			enterPaused();
			break;
		case PAUSED:
			break; // Do nothing.
		default:
			throw new InvalidEventException();
		}
	}

	synchronized public void resume() {
		if (isDestroyed == true)
			throw new IllegalStateException();
		switch (this.state) {
		case ACTIVE:
			break; // Do nothing.
		case PAUSED:
			enterActive();
			break;
		default:
			throw new InvalidEventException();
		}
	}

	synchronized public void stop() {
		if (isDestroyed == true)
			throw new IllegalStateException();

		if (state == State.ACTIVE || state == State.PAUSED) {
			enterStopped();
		}
		// else {
		// throw new InvalidEventException();
		// }
	}

	private void error() {
		assert(isDestroyed == false);
		enterError(null);
	}

	private void error(Exception e) {
		assert(isDestroyed == false);
		enterError(e);
	}

	/* **************************** Destroyable **************************** */

	/* NB: this can be read by many threads, hence volatile. */
	private volatile boolean isDestroyed = false;

	@Override
	synchronized public void destroy() throws DestroyFailedException {
		// TODO: Finish
		isDestroyed = true;
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}

}
