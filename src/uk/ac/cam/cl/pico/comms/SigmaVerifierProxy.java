/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.comms;

import java.io.IOException;

import uk.ac.cam.cl.pico.crypto.ISigmaVerifier;
import uk.ac.cam.cl.pico.crypto.messages.EncPicoAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncServiceAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncStatusMessage;
import uk.ac.cam.cl.pico.crypto.messages.StartMessage;

public abstract class SigmaVerifierProxy implements ISigmaVerifier {

	protected final MessageSerializer serializer;
	private boolean initialised = false;

	public SigmaVerifierProxy(final MessageSerializer serializer) {
		this.serializer = serializer;
	}

	/**
	 * Subclasses may override this method if they want to lazily initialise resources immediately 
	 * before the first message through this proxy.
	 * 
	 * @throws IOException if an <code>IOException</code> occurs during the initialisation.
	 */
	protected void lazyInit() throws IOException {}

	/**
	 * Ensure lazy initialisation has taken place.
	 * 
	 * <p>This method will not call {@link #lazyInit()} again, if it has already been called.
	 * 
	 * @throws IOException if an <code>IOException</code> occurs during the initialisation.
	 */
	protected final void ensureInitialised() throws IOException {
		if (!initialised) {
			lazyInit();
		}
		initialised = true;
	}

	/**
	 * Send a serialized message to the remote verifier and return its serialized response.
	 * 
	 * @param serializedMessage the serialized message to send.
	 * @return the remote verifier's serialized response.
	 * @throws IOException if there is a problem communicating with the remote verifier.
	 */
	protected abstract byte[] getResponse(byte[] serializedMessage) throws IOException;

	@Override
	public final EncServiceAuthMessage start(final StartMessage msg) throws IOException {
	    final byte[] serializedMsg = serializer.serialize(msg, StartMessage.class);
	    ensureInitialised();
	    final byte[] serializedResponse = getResponse(serializedMsg);
	    return serializer.deserialize(serializedResponse, EncServiceAuthMessage.class);
	}

	@Override
	public final EncStatusMessage authenticate(EncPicoAuthMessage msg) throws IOException {
	    final byte[] serializedMsg = serializer.serialize(msg, EncPicoAuthMessage.class);
	    ensureInitialised();
	    final byte[] serializedResponse = getResponse(serializedMsg);
	    return serializer.deserialize(serializedResponse, EncStatusMessage.class);
	}
}