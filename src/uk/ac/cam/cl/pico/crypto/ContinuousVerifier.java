/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.pico.crypto.messages.EncryptedMessage.FieldDeserializationException;
import uk.ac.cam.cl.pico.crypto.messages.ReauthState;
import uk.ac.cam.cl.pico.crypto.messages.SequenceNumber;
import uk.ac.cam.cl.pico.crypto.messages.EncPicoReauthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncServiceReauthMessage;
import uk.ac.cam.cl.pico.crypto.messages.PicoReauthMessage;
import uk.ac.cam.cl.pico.crypto.messages.ServiceReauthMessage;

/**
 * Verifier state machine for the continuous authentication protocol.
 * 
 * On each reauth, the sequence number is checked against the previous sequence number, and a new
 * sequence number is generated and both stored and sent.
 * 
 * <pre>
 *                                      ╭──────╮
 *                         pause        │      │pause
 *           ╭─────╮  ╭───────────▶ Paused ◀───╯
 *  continue │     │  │             │ │ │
 *           ╰──▶ Active ◀──────────╯ │ │
 *                 │  │    continue   │ │
 *            stop │  ╰───────╮       │ │ timeout
 *                 │  ╭───────┼───────╯ │
 *                 │  │ stop  ╰───────╮ │         ┆ ┆ ┆ ┆
 *                 │  │       timeout │ │         │ │ │ │
 *                 ▼  ▼               ▼ ▼         ▼ ▼ ▼ ▼
 *               Stopped            Timeout        Error
 * </pre>
 * 
 * NB: Error state can be entered from any state.
 * 
 * There is a layer of indirection, there is one message method that can trigger a pause, continue
 * or stop event.
 * 
 * <p>
 * This class follows a state machine design pattern. There are a well defined set of internal
 * states and for each state there is a <code>private</code> "state entry method". The state entry
 * methods are the only way to change the internal state of the prover. The public interface of the
 * prover consists of "event methods". These methods define the transition function of the prover
 * state machine, either calling a state entry method, doing nothing, or raising an
 * <code>{@link InvalidEventException}</code>, depending on the current state of the prover.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * @author Chris Warrington <cw471@cl.cam.ac.uk>
 * 
 */
final public class ContinuousVerifier implements IContinuousVerifier,
        Destroyable {

    public static final int activeTimeout = 10000, // 10 seconds
            pausedTimeout = 50000, // 50 seconds
            timeoutLeeway = 10000; // 10 seconds;

    private final SecretKey sessionEncryptionKey;
    private final IContinuousVerifier.Client clientInterface;
    private final PublicKey picoPublicKey;

    private SequenceNumber currentSequenceNumber;
    private double timeoutTimestamp;
    private State state = State.ACTIVE;

    private final Logger LOGGER = LoggerFactory
            .getLogger(ContinuousVerifier.class);

    public ContinuousVerifier(final SecretKey sessionEncryptionKey,
            final SequenceNumber initialSequenceNumber,
            final IContinuousVerifier.Client clientInterface,
            final PublicKey picoPublicKey) {

        // Verify the method's preconditions
        if (sessionEncryptionKey == null)
            throw new NullPointerException();
        if (initialSequenceNumber == null)
            throw new NullPointerException();
        if (clientInterface == null)
            throw new NullPointerException();
        if (picoPublicKey == null)
            throw new NullPointerException();

        this.sessionEncryptionKey = sessionEncryptionKey;
        this.currentSequenceNumber = initialSequenceNumber;
        this.clientInterface = clientInterface;
        this.picoPublicKey = picoPublicKey;
        setTimeout(activeTimeout + timeoutLeeway);
    }

    /* *************** State Entry Methods *************** */

    private void enterActive() {

        // Verify the method's preconditions
        assert (isDestroyed == false);

        state = State.ACTIVE;
        LOGGER.info("Enter Active");
        clientInterface.onResume(picoPublicKey);
    }

    private void enterPaused() {

        // Verify the method's preconditions
        assert (isDestroyed == false);

        state = State.PAUSED;
        LOGGER.info("Enter Paused");
        clientInterface.onPause(picoPublicKey);
    }

    private void enterStopped() {

        // Verify the method's preconditions
        assert (isDestroyed == false);

        state = State.STOPPED;
        LOGGER.info("Enter Stopped");
        clientInterface.onStop(picoPublicKey);
    }

    private void enterTimeout() {

        // Verify the method's preconditions
        assert (isDestroyed == false);

        state = State.TIMEOUT;
        LOGGER.info("Enter Timeout");
        clientInterface.onStop(picoPublicKey);
    }

    private void enterError() {

        // Verify the method's preconditions
        assert (isDestroyed == false);

        state = State.ERROR;
        LOGGER.error("Enter Error");
        clientInterface.onStop(picoPublicKey);
    }

    /* *************** Event Methods *************** */

    private void cont() {
        switch (this.state) {
            case ACTIVE:
                break;
            case PAUSED:
                enterActive();
                break;
            default:
                throw new InvalidEventException();
        }
    }

    private void pause() {
        switch (this.state) {
            case ACTIVE:
                enterPaused();
                break;
            case PAUSED:
                break;
            default:
                throw new InvalidEventException();
        }
    }

    private void stop() {
        switch (this.state) {
            case ACTIVE:
            case PAUSED:
                enterStopped();
                break;
            default:
                throw new InvalidEventException();
        }
    }

    private void timeout() {
        switch (this.state) {
            case ACTIVE:
            case PAUSED:
                enterTimeout();
                break;
            default:
                throw new InvalidEventException();
        }
    }

    private void error() {

        assert (isDestroyed == false);
        System.out.println("Called private error() method");
        enterError();
    }

    @Override
    public EncServiceReauthMessage reauth(final EncPicoReauthMessage msg)
            throws IOException {

        // Verify the method's preconditions
        if (msg == null)
            throw new NullPointerException();
        if (isDestroyed == true)
            throw new IllegalStateException();

        if (state == State.ACTIVE || state == State.PAUSED) {

            if (isTimedout()) {
                LOGGER.info("Timed out");
                timeout();
            }

            // Try to decrypt the PicoReauthMessage, state will be set to ERROR if this fails.
            PicoReauthMessage m = null;
			try {
				m = msg.decrypt(sessionEncryptionKey);
			} catch (InvalidKeyException e) {
				// Re-thrown unchecked because this signals incompatibility between the session
				// encryption key and the chosen encryption cipher. This is a configuration error
				// and is considered a fatal error from which the program probably can't (and
				// shouldn't try to) recover.
				throw new CryptoRuntimeException(e);
			} catch (InvalidAlgorithmParameterException e) {
				// Any of the other exceptions caught here indicate that the decryption failed due
				// to some error on the part of the prover.
				LOGGER.error("InvalidAlgorithmParameter Exception");
				error();
			} catch (IllegalBlockSizeException e) {
				LOGGER.error("IllegalBlockSize Exception");
				error();
			} catch (BadPaddingException e) {
				LOGGER.error("BadPadding Exception");
				error();
			} catch (FieldDeserializationException e) {
				LOGGER.error("FieldDeserialization Exception");
				error();
			}
			
			// Start creating response message. Note, reauth verification does not occur until the
			// else block below
            final ReauthState responseType;
            final int timeout;
			
			if (state == State.ERROR) {
				// Decryption failed, or session timed out.
				responseType = ReauthState.ERROR;
				timeout = -1;
			} else {
				// Verify reauthentication
				final SequenceNumber n = m.getSequenceNumber();
	            if (this.currentSequenceNumber.verifyResponse(n)) {
	            	// Reauthentication successful!
	            	
	            	// Increment the sequence number
	                this.currentSequenceNumber = n.getResponse();
	                
	                switch (m.getReauthState()) {
	                    case CONTINUE:
	                        cont();
	                        responseType = ReauthState.CONTINUE;
	                        timeout = activeTimeout;
	                        break;
	                    case PAUSE:
	                        pause();
	                        responseType = ReauthState.PAUSE;
	                        timeout = pausedTimeout;
	                        break;
	                    case STOP:
	                        stop();
	                        responseType = ReauthState.STOP;
	                        timeout = -1;
	                        break;
	                    default:
	                        error();
	                        responseType = ReauthState.ERROR;
	                        timeout = -1;
	                        break;
	                }
	            } else {
	            	// Reauthentication failed
	            	System.out.println("Reauthentication failed, couldn't verify sequence number " + currentSequenceNumber.toString() + " against " + n.toString());
	                error();
	                responseType = ReauthState.ERROR;
	                timeout = -1;
	            }
			}
			
			final ServiceReauthMessage serviceReauth = new ServiceReauthMessage(
					msg.getSessionId(), responseType, timeout, currentSequenceNumber);
            EncServiceReauthMessage ercm;
			try {
				ercm = serviceReauth.encrypt(sessionEncryptionKey);
			} catch (InvalidKeyException e) {
				// Re-thrown unchecked because this signals incompatibility between the session
				// encryption key and the chosen encryption cipher. This is a configuration error
				// and is considered a fatal error from which the program probably can't (and
				// shouldn't try to) recover.
				throw new CryptoRuntimeException(e);
			}
            setTimeout(timeout + timeoutLeeway);
            return ercm;
        } else {
            throw new InvalidEventException();
        }
    }

    /* *************** Helper Methods *************** */

    private boolean isTimedout() {

        assert (isDestroyed == false);

        return timeoutTimestamp < System.currentTimeMillis();
    }

    private void setTimeout(int timeout) {

        assert (isDestroyed == false);

        timeoutTimestamp = System.currentTimeMillis() + timeout;
    }

    /* *************** Destroyable *************** */
    private boolean isDestroyed = false;

    @Override
    public void destroy() throws DestroyFailedException {

        // Verify the method's preconditions
        if (isDestroyed == true)
            throw new IllegalStateException();

        isDestroyed = true;
        // TODO actually destroy sensitive data.
    }

    @Override
    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    @Override
	public State getState() {

        // Verify the method's preconditions
        if (isDestroyed == true)
            throw new IllegalStateException();

        return state;
    }

}
