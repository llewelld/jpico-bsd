package uk.ac.cam.cl.pico.gson;

import static org.junit.Assert.assertEquals;

import java.security.PublicKey;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;
import uk.ac.cam.cl.pico.util.UsesCryptoTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PublicKeyGsonSerializerTest extends UsesCryptoTest {

    private static Gson gson;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        // Set up Gson instance with the right adapter registered
        gson = new GsonBuilder()
                .registerTypeAdapter(byte[].class, new ByteArrayGsonSerializer())
                .registerTypeAdapter(PublicKey.class, new PublicKeyGsonSerializer())
                .disableHtmlEscaping()
                .create();
    }

    private static PublicKey key;

    @Before
    public void setUp() throws Exception {
		key = CryptoFactory.INSTANCE.ecKpg().generateKeyPair().getPublic();
    }

    @Test
    public void testSame() throws Exception {
        String json1 = gson.toJson(key, PublicKey.class);
        String json2 = gson.toJson(key, PublicKey.class);
        assertEquals(json1, json2);
    }

    @Test
    public void testCycle() throws Exception {
        String json = gson.toJson(key, PublicKey.class);
        assertEquals(key, gson.fromJson(json, PublicKey.class));
    }
}
