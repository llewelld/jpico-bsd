package uk.ac.cam.cl.pico.visualcode;

import static org.junit.Assert.fail;

import java.security.KeyPair;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;
import uk.ac.cam.cl.pico.util.UsesCryptoTest;

public class NewKeyVisualCodeTest extends UsesCryptoTest {

    @SuppressWarnings("unused")
	private KeyPair keyPair;

    @Before
    public void setUp() throws Exception {
		keyPair = CryptoFactory.INSTANCE.ecKpg().generateKeyPair();
    }

    @Test
    public void testNewPubKeyPairingInstance() throws Exception {
        /*
         * final URI serviceUri = new URI("http://www.example.org:5001"); final Map<String, String>
         * credentials = new HashMap<String, String>(); credentials.put("username", "gcj21");
         * credentials.put("password", "password"); final VisualCode visualCode = new
         * KeyPairingVisualCode( "ServiceName", serviceUri, keyPair.getPublic());
         * 
         * assertNotNull(visualCode);
         */
        fail("Needs re-implementing");
    }

    @Test
    public void testNewPubKeyAuthenticationInstance() throws Exception {
        /*
         * final URI serviceUri = new URI("http://www.example.org:5001"); final VisualCode
         * visualCode = new KeyAuthenticationVisualCode( "ServiceName", serviceUri,
         * keyPair.getPublic());
         * 
         * assertNotNull(visualCode);
         */
        fail("Needs re-implementing");
    }

}
