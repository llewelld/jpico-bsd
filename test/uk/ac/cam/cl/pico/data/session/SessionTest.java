package uk.ac.cam.cl.pico.data.session;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.crypto.SimpleAuthToken;
import uk.ac.cam.cl.pico.data.DataFactory;
import uk.ac.cam.cl.pico.data.pairing.Pairing;
import uk.ac.cam.cl.pico.data.pairing.PairingTest;
import uk.ac.cam.cl.pico.data.test.TestDataFactory;
import uk.ac.cam.cl.pico.util.UsesCryptoTest;

public class SessionTest extends UsesCryptoTest {

    public static final DataFactory DATA_FACTORY = new TestDataFactory();
    public static final SessionImpFactory FACTORY = DATA_FACTORY;
    public static final String REMOTE_ID = "session remote ID";
    public static final SecretKey SECRET_KEY = new SecretKeySpec("secret".getBytes(), "AES");
    public static final Pairing PAIRING = PairingTest.getPairing();
    public static final AuthToken AUTH_TOKEN = new SimpleAuthToken("auth");

    @Test
    public void testSessionSessionImpFactorySession() {
        fail("Not yet implemented");
    }

    @Test
    public void testNewInstanceActive() {
        Session s = Session.newInstanceActive(
                FACTORY, REMOTE_ID, SECRET_KEY, PAIRING, AUTH_TOKEN);
        assertNotNull(s);
    }

    @Test
    public void testNewInstanceClosed() {
        fail("Not yet implemented");
    }

    @Test
    public void testNewInstanceInError() {
        fail("Not yet implemented");
    }

    @Test
    public void testEqualsObject() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetStatus() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetError() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetLastAuthDate() {
        fail("Not yet implemented");
    }

    @Test
    public void testHasAuthToken() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetAuthToken() {
        fail("Not yet implemented");
    }

    @Test
    public void testCheckRemoteIdNull() {
        fail("Not yet implemented");
    }

    @Test
    public void testCheckRemoteIdEmpty() {
        fail("Not yet implemented");
    }

    @Test
    public void testCheckLastAuthDateNull() {
        fail("Not yet implemented");
    }

    @Test
    public void testCheckLastAuthDateFuture() {
        fail("Not yet implemented");
    }
}
