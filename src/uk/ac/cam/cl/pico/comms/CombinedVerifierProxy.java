/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.comms;

import java.io.IOException;

import uk.ac.cam.cl.pico.crypto.ICombinedVerifier;
import uk.ac.cam.cl.pico.crypto.messages.EncPicoReauthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncServiceReauthMessage;

/**
 * Abstract base class for proxies of remote combined verifiers.
 * 
 * <p>Subclasses must provide an implementation of {@link #getResponse(byte[]) sendRequest} method
 * which transmits a serialized message to the remote verifier and returns its serialized response.
 * Subclasses should pass an appropriate {@link MessageSerializer} to the super constructor.
 * 
 * @see MessageSerializer
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
public abstract class CombinedVerifierProxy extends SigmaVerifierProxy implements ICombinedVerifier {

    protected CombinedVerifierProxy(final MessageSerializer serializer) {
    	super(serializer);
    }

	@Override
    public final EncServiceReauthMessage reauth(EncPicoReauthMessage msg) throws IOException {
        final byte[] serializedMsg = serializer.serialize(msg, EncPicoReauthMessage.class);
        ensureInitialised();
        final byte[] serializedResponse = getResponse(serializedMsg);
        return serializer.deserialize(serializedResponse, EncServiceReauthMessage.class);
    }

	@Override
	public State getState() {
		throw new UnsupportedOperationException("getState not supported by verifier proxies");
	}
}
