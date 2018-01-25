package uk.ac.cam.cl.pico.crypto.messages;

import javax.crypto.KeyGenerator;

import org.junit.BeforeClass;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;
import uk.ac.cam.cl.pico.util.UsesCryptoTest;

public class MessageTest extends UsesCryptoTest {

    protected static KeyGenerator macKg;
    protected static KeyGenerator encKg;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        // Set up some more key generators for symmetric keys
        //encKg = KeyGenerator.getInstance("AES", "BC");
        //encKg.init(256);
        encKg = CryptoFactory.INSTANCE.aes256Kg();
        //macKg = KeyGenerator.getInstance("HMACSHA256", "BC");
        macKg = CryptoFactory.INSTANCE.sha256HmacKg();
    }

    public MessageTest() {
        super();
    }
}
