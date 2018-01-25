/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.KeyPair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.pico.crypto.ContinuousProver.ProverStateChangeNotificationInterface;
import uk.ac.cam.cl.pico.crypto.ContinuousProver.SchedulerInterface;
import uk.ac.cam.cl.pico.crypto.NewSigmaProver.ProverAuthRejectedException;
import uk.ac.cam.cl.pico.crypto.NewSigmaProver.VerifierAuthFailedException;
import uk.ac.cam.cl.pico.crypto.messages.ReauthState;
import uk.ac.cam.cl.pico.crypto.messages.ReauthState.InvalidReauthStateIndexException;
import uk.ac.cam.cl.pico.crypto.messages.SequenceNumber;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataInputStream;
import uk.ac.cam.cl.pico.data.pairing.KeyPairing;
import uk.ac.cam.cl.pico.data.session.Session;
import uk.ac.cam.cl.pico.data.session.SessionImpFactory;

import com.google.common.base.Optional;

@Deprecated
final public class ServiceSigmaProver {

    private static final byte VERSION_1_0 = (byte) 1;

    private final static Logger LOGGER =
            LoggerFactory.getLogger(ServiceSigmaProver.class.getSimpleName());
    
    private final KeyPairing pairing;
    private final SessionImpFactory sessionFactory;
    private final NewSigmaProver prover;
    private Optional<SequenceNumber> sequenceNumber = Optional.absent();

    public ServiceSigmaProver(
            final KeyPairing pairing,
            final ISigmaVerifier verifier,
            final SessionImpFactory sessionFactory) {
        this.pairing = checkNotNull(pairing, "pairing cannot be null");
        checkNotNull(verifier, "verifier cannot be null");
        this.sessionFactory = checkNotNull(sessionFactory, "sessionFactory cannot be null");
        
    	final KeyPair kp = new KeyPair(pairing.getPublicKey(), pairing.getPrivateKey());
    	final byte[] commit = pairing.getService().getCommitment();
    	prover = new NewSigmaProver(VERSION_1_0, kp, null, verifier, commit);
    }

    public ContinuousProver getContinuousProver(
    		final IContinuousVerifier verifier,
            final Session session,
            final ProverStateChangeNotificationInterface continuousClient,
            final SchedulerInterface scheduler) {
    	if (sequenceNumber.isPresent()) {
    		return new ContinuousProver(
    				session, verifier, continuousClient, scheduler, sequenceNumber.get());
    	} else {
    		throw new IllegalStateException("verifier has not assigned a sequence number");
    	}
    }

    public Session startSession() throws CryptoRuntimeException {
    	try {
    		// Use actual new prover instance. Return value signals whether continuous is supported
    		// or not.
    		final boolean continuous = prover.prove();
    		
    		// Wrap extra received data in input stream
    		LengthPrependedDataInputStream los = new LengthPrependedDataInputStream(
    				new DataInputStream(new ByteArrayInputStream(prover.getReceivedExtraData())));
    		
    		// Read auth token
    		final byte[] authTokenBytes = los.readVariableLengthByteArray();
    		final AuthToken authToken = AuthTokenFactory.fromByteArray(authTokenBytes);
    		
    		if (continuous) {
    			// Read reauth state, sequence number and timeout as well
    			ReauthState state;
				try {
					state = ReauthState.fromByte(los.readByte());
				} catch (InvalidReauthStateIndexException e) {
					return Session.newInstanceInError(
                    		sessionFactory, pairing, Session.Error.SERVICE_REPORTED_ERROR);
				}
    			sequenceNumber = Optional.of(
    					SequenceNumber.fromByteArray(los.readVariableLengthByteArray()));
    			final int timeout = los.readInt();
    			
    			switch(state) {
    				case CONTINUE:
    					return Session.newInstanceActive(
                                sessionFactory,
                                Integer.toString(prover.getVerifierSessionId()),
                                prover.getSharedKey(),
                                pairing,
                                authToken);
    				case PAUSE:
    					return Session.newInstancePaused(
                                sessionFactory,
                                Integer.toString(prover.getVerifierSessionId()),
                                prover.getSharedKey(),
                                pairing,
                                authToken);
					case STOP:
    					return Session.newInstanceClosed(
                                sessionFactory,
                                Integer.toString(prover.getVerifierSessionId()),
                                pairing,
                                authToken);
					case ERROR:
					default:
	                    return Session.newInstanceInError(
	                    		sessionFactory, pairing, Session.Error.SERVICE_REPORTED_ERROR);
    			}                    
    		} else {
    			return Session.newInstanceClosed(
    					sessionFactory,
    					Integer.toString(prover.getVerifierSessionId()),
    					pairing,
    					authToken);
    		}
		} catch (IOException e) {
			fail(e);
            return Session.newInstanceInError(
            		sessionFactory, pairing, Session.Error.IO_EXCEPTION);
		} catch (ProverAuthRejectedException e) {
			fail(e);
            return Session.newInstanceInError(
            		sessionFactory, pairing, Session.Error.SERVICE_REPORTED_ERROR);
		} catch (ProtocolViolationException e) {
			fail(e);
			// Not really an appropriate session error state, but best there is at the moment
            return Session.newInstanceInError(
            		sessionFactory, pairing, Session.Error.SERVICE_REPORTED_ERROR);
		} catch (VerifierAuthFailedException e) {
			fail(e);
            return Session.newInstanceInError(
            		sessionFactory, pairing, Session.Error.SERVICE_AUTHENTICATION_FAILURE);
		}
    }

    public SequenceNumber getSequenceNumber() {
    	if (sequenceNumber.isPresent()) {
    		return sequenceNumber.get();
    	} else {
    		throw new IllegalStateException("verifier has not assigned a sequence number");
    	}
    }

    private void fail(final Exception e) {
        LOGGER.debug("Authentication to service failed", e);
    }

    private void fail(final String reason) {
        LOGGER.debug(reason);
    }
}
