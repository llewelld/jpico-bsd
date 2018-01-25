/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import java.io.IOException;
import java.security.PublicKey;

import uk.ac.cam.cl.pico.crypto.messages.EncServiceReauthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncPicoReauthMessage;

public interface IContinuousVerifier {

    public static enum State {
	    ACTIVE,
	    PAUSED,
	    STOPPED,
	    TIMEOUT,
	    ERROR;
	}

    /**
	 * Callback interface to be implemented by the Pico server when the client service application
	 * needs to be notified of continuous authentication events.
	 * 
	 * <p>
	 * This interface is used in the same way as {@link ISigmaVerifier.Client}, see its
	 * documentation for an example.
	 * 
	 * @author Max Spencer <ms955@cl.cam.ac.uk>
	 * 
	 */
	public interface Client {
	    public abstract void onPause(PublicKey picoPublicKey);
	
	    public abstract void onStop(PublicKey picoPublicKey);
	
	    public abstract void onResume(PublicKey picoPublicKey);
	}

	/** 
     * @return the current state of the verifier.
     * @throws UnsupportedOperationException if specific verifier implementation does not support
     *  this method.
     */
    State getState() throws UnsupportedOperationException;

	/**
     * As part of a continuous authentication session, increment the sequence number from the
     * verifier.
     * 
     * @param msg the message including the current sequence number.
     * @return the response message, including the incremented sequence number.
     * @throws IOException
     */
    EncServiceReauthMessage reauth(EncPicoReauthMessage msg) throws IOException;
}
