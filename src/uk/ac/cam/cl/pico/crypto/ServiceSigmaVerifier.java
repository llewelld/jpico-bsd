/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.SecretKey;

import uk.ac.cam.cl.pico.crypto.messages.EncPicoAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncServiceAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncStatusMessage;
import uk.ac.cam.cl.pico.crypto.messages.ReauthState;
import uk.ac.cam.cl.pico.crypto.messages.SequenceNumber;
import uk.ac.cam.cl.pico.crypto.messages.StartMessage;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataOutputStream;

import com.google.common.base.Optional;

@Deprecated
public class ServiceSigmaVerifier implements ISigmaVerifier {
	
	public interface Client {
		public AuthToken onAuthenticate(PublicKey proverPublicKey) throws IOException;
	}
	
	private static class ClientAdapter implements ISigmaVerifier.Client {
		
		private final ServiceSigmaVerifier.Client client;
		private final boolean startContinuous;
		private Optional<SequenceNumber> sequenceNumber = Optional.absent();
		
		public ClientAdapter(ServiceSigmaVerifier.Client client, boolean startContinuous) {
			this.client = client;
			this.startContinuous = startContinuous;
		}

		/*
		 * Because the old SigmaVerifier class which ServiceSigmaVerifier was adapted from had no
		 * client authorisation facility (a mecahnism for the client to reject authentications
		 * based on the prover's public key and extra data), this method will always return an
		 * accepting authorisation (*). Specifically, a return value of null from 
		 * ServiceSigmaVerifier.Client#onAuthenticate is taken to mean that there is simply no 
		 * auth token to send and NOT that the client is rejecting the authentication based on the
		 * prover's public key. 
		 */
		@Override
		public ClientAuthorisation onAuthenticate(
				PublicKey picoPublicKey, byte[] receivedExtraData) throws IOException {
			ByteArrayOutputStream baos = null;
			LengthPrependedDataOutputStream los = null;
			try {
				baos = new ByteArrayOutputStream();
				los = new LengthPrependedDataOutputStream(new DataOutputStream(baos));
				
				// First include the serialized auth token in the extra data. Check if the returned
				// auth token value is null to be on the safe side.
				final AuthToken authToken = client.onAuthenticate(picoPublicKey);
				final byte[] authTokenBytes;
				if (authToken != null) {
					authTokenBytes = authToken.toByteArray();
				} else {
					authTokenBytes = new byte[0];
				}
				// Note: Always write length even when it's zero, so that it gets unpacked 
				// correctly by the client. 
				los.writeVariableLengthByteArray(authTokenBytes);
				
				// Then, if continuous should be started, add the reauth state, sequence number and 
				// timeout
				if (startContinuous) {
					final ReauthState state = ReauthState.CONTINUE;
					sequenceNumber = Optional.of(SequenceNumber.getRandomInstance());
					final int timeout = ContinuousVerifier.activeTimeout;
					los.writeByte(state.toByte());
					los.writeVariableLengthByteArray(sequenceNumber.get().toByteArray());
					los.writeInt(timeout);
				}
				
				// * Accepting authorisation always returned.
				return ClientAuthorisation.accept(baos.toByteArray());
			} finally {
				if (los != null) {
					los.close();
				}
			}
		}

		public SequenceNumber getSequenceNumber() {
			if (sequenceNumber.isPresent()) {
				return sequenceNumber.get();
			} else {
				throw new IllegalStateException("invalid operation before authentication");
			}
		}
		
	}

    private final boolean startContinuous;
    private final ClientAdapter clientAdapter;
    private final NewSigmaVerifier verifier;
    
    public ServiceSigmaVerifier(KeyPair serviceIdKeyPair, ServiceSigmaVerifier.Client client) {
    	this(serviceIdKeyPair, client, true);
    }

    public ServiceSigmaVerifier(KeyPair serviceIdKeyPair, ServiceSigmaVerifier.Client client, boolean startContinuous) {
    	this.startContinuous = startContinuous;
    	clientAdapter = new ClientAdapter(client, startContinuous);
    	verifier = new NewSigmaVerifier(
    			NewSigmaProver.VERSION_1_1,
    			serviceIdKeyPair,
    			new SecureRandom().nextInt(),
    			clientAdapter,
    			startContinuous);
    }
    
    /**
     * Convenience factory method for creating a continuous verifier for the session this verifier
     * was used to authenticate.
     *  
     * @param continuousClient callback interface for the continuous verifier.
     * @return appropriate continuous verifier for this session
     * @throws IllegalStateException if this method is called before the authentication has
     * 	completed, or if this verifier is not configured to start continuous authentication
     * 	sessions.
     */
    public IContinuousVerifier getContinuousVerifier(
    		final IContinuousVerifier.Client continuousClient) {
    	if (startContinuous) {
    		return new ContinuousVerifier(
    				verifier.getSharedKey(),
    				clientAdapter.getSequenceNumber(),
    				continuousClient,
    				verifier.getProverIdPubKey());
    	} else {
    		throw new IllegalStateException("not configured to start continuous auth sessions");
    	}
    }
    
    @Override
    public EncServiceAuthMessage start(StartMessage startMessage) {
        try {
			return verifier.start(startMessage);
		} catch (ProtocolViolationException e) {
			return null;
		}
    }

    @Override
    public EncStatusMessage authenticate(EncPicoAuthMessage encAuthMessage) {
        try {
			return verifier.authenticate(encAuthMessage);
		} catch (IOException e) {
			return null;
		} catch (ProtocolViolationException e) {
			return null;
		}
    }

    public PublicKey getPicoAccountIdentityPublicKey() {
        return verifier.getProverIdPubKey();
    }

    public SecretKey getSessionEncryptionKey() {
        return verifier.getSharedKey();
    }

    public SequenceNumber getSequenceNumber() {
        return clientAdapter.getSequenceNumber();
    }
}
