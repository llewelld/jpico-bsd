/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import java.io.IOException;
import java.security.PublicKey;

import uk.ac.cam.cl.pico.crypto.messages.EncPicoAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncServiceAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncStatusMessage;
import uk.ac.cam.cl.pico.crypto.messages.StartMessage;

public interface ISigmaVerifier {

    /**
	 * Callback interface to be implemented by the Pico server when the client service application
	 * needs to be notified of authentication events.
	 * 
	 * <p>As an example, consider a web site using Pico authentication. The service consists of two
	 * separate components:
	 * 
	 * <ol>
	 * <li>The web server serving the web site itself which is the "client service application" and
	 * is what the user interacts with.</li>
	 * <li>The Pico server which communicates with the users' Picos.</li>
	 * </ol>
	 * 
	 * <p>In this case the Pico server would include a concrete SigmaClientInterface implementation
	 * capable of notifying the web server of the relevant events using some kind of inter-process
	 * communication mechanism.
	 * 
	 * @see ContinuousVerifier#ContinuousClient
	 * @author Max Spencer <ms955@cl.cam.ac.uk>
	 * 
	 */
	public interface Client {
		
		public static class ClientAuthorisation {
			// Singletons for simple common return values
			private static final ClientAuthorisation emptyAccept =
					new ClientAuthorisation(true, null);
			private static final ClientAuthorisation reject =
					new ClientAuthorisation(false, null);
			
			public static ClientAuthorisation accept(byte[] extraData) {
				return new ClientAuthorisation(true, extraData);
			}
			public static ClientAuthorisation accept() {
				return emptyAccept;
			}
			
			public static ClientAuthorisation reject() {
				return reject;
			}
			
			private final boolean authorised;
			private final byte[] extraData;
			
			private ClientAuthorisation(boolean a, byte[] d) {
				authorised = a;
				extraData = d;
			}
			
			public boolean authorised() {
				return authorised;
			}

			public byte[] extraData() {
				return extraData;
			}
		}
		
	    public ClientAuthorisation onAuthenticate(PublicKey picoPublicKey, byte[] receivedExtraData)
	    		throws IOException;
	}

	/**
     * Start an authentication session with the verifier by sending a {@link StartMessage}.
     * 
     * @param msg the Start Message to be processed by the verifier.
     * @return the verifier's response. This method never returns <code>null</code>.
     * @throws ProtocolViolationException if the prover violated the protocol.
     * @throws IOException
     */
    public abstract EncServiceAuthMessage start(StartMessage msg) 
    		throws IOException, ProtocolViolationException;

    /**
     * Authenticate to the verifier by sending an {@link EncPicoAuthMessage}.
     * 
     * @param msg the encrypted Prover Authentication Message to be processed by the verifier.
     * @return the verifier's response.
     * @throws ProtocolViolationException if the prover violated the protocol.
     * @throws IOException
     */
    public abstract EncStatusMessage authenticate(EncPicoAuthMessage msg) 
    		throws IOException, ProtocolViolationException;
}
