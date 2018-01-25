package uk.ac.cam.cl.pico.gson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.cam.cl.pico.util.UsesCryptoTest;
import uk.ac.cam.cl.pico.visualcode.DelegatePairingVisualCode;
import uk.ac.cam.cl.pico.visualcode.DelegatePairingVisualCodeTest;
import uk.ac.cam.cl.pico.visualcode.KeyAuthenticationVisualCode;
import uk.ac.cam.cl.pico.visualcode.KeyPairingVisualCodeTest;
import uk.ac.cam.cl.pico.visualcode.LensAuthenticationVisualCode;
import uk.ac.cam.cl.pico.visualcode.LensAuthenticationVisualCodeTest;
import uk.ac.cam.cl.pico.visualcode.LensPairingVisualCode;
import uk.ac.cam.cl.pico.visualcode.LensPairingVisualCodeTest;
import uk.ac.cam.cl.pico.visualcode.SignedVisualCode;
import uk.ac.cam.cl.pico.visualcode.VisualCode;

import com.google.gson.Gson;

public class VisualCodeGsonTest extends UsesCryptoTest {

    private static Gson GSON;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	GSON = VisualCodeGson.gson;
    }

    // Lens auth visual code

    @Test
    public void testLensAuthenticationSerialize() throws Exception {
        String json = GSON.toJson(LensAuthenticationVisualCodeTest.getCode());
        assertNotNull(json);
        assertTrue(json.length() > 0);
    }

    @Test
    public void testLensAuthenticationSame() throws Exception {
        LensAuthenticationVisualCode vc = LensAuthenticationVisualCodeTest.getCode();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(vc);
        assertEquals(json1, json2);
    }

    @Test
    public void testLensAuthenticationCycle() throws Exception {
        LensAuthenticationVisualCode vc = LensAuthenticationVisualCodeTest.getCode();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(GSON.fromJson(GSON.toJson(vc), VisualCode.class));
        assertEquals(json1, json2);
    }

    // Lens pairing visual code

    // - Some credentials

    @Test
    public void testLensPairingSomeSerialize() throws Exception {
        String json = GSON.toJson(LensPairingVisualCodeTest.getCodeSome());
        assertNotNull(json);
    }

    @Test
    public void testLensPairingSomeSame() throws Exception {
        LensPairingVisualCode vc = LensPairingVisualCodeTest.getCodeSome();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(vc);
        assertEquals(json1, json2);
    }

    @Test
    public void testLensPairingSomeCycle() throws Exception {
        LensPairingVisualCode vc = LensPairingVisualCodeTest.getCodeSome();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(GSON.fromJson(GSON.toJson(vc), VisualCode.class));
        assertEquals(json1, json2);
    }

    // - No credentials

    @Test
    public void testLensPairingNoSerialize() throws Exception {
        String json = GSON.toJson(LensPairingVisualCodeTest.getCodeNone());
        assertNotNull(json);
    }

    @Test
    public void testLensAuthenticationNoSame() throws Exception {
        LensPairingVisualCode vc = LensPairingVisualCodeTest.getCodeNone();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(vc);
        assertEquals(json1, json2);
    }

    @Test
    public void testLensAuthenticationNoCycle() throws Exception {
        LensPairingVisualCode vc = LensPairingVisualCodeTest.getCodeNone();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(GSON.fromJson(GSON.toJson(vc), VisualCode.class));
        assertEquals(json1, json2);
    }

    // Key auth visual code

    @Test
    public void testKeyAuthenticationSerialize() throws Exception {
        String json = GSON.toJson(KeyAuthenticationVisualCodeTest.getCode());
        assertNotNull(json);
    }

    @Test
    public void testKeyAuthenticationSame() throws Exception {
        KeyAuthenticationVisualCode vc = KeyAuthenticationVisualCodeTest.getCode();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(vc);
        assertEquals(json1, json2);
    }

    @Test
    public void testKeyAuthenticationCycle() throws Exception {
        KeyAuthenticationVisualCode vc = KeyAuthenticationVisualCodeTest.getCode();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(GSON.fromJson(GSON.toJson(vc), VisualCode.class));
        assertEquals(json1, json2);
    }

    // Key pairing visual code

    @Test
    public void testKeyPairingSerialize() throws Exception {
        String json = GSON.toJson(KeyPairingVisualCodeTest.getCode());
        assertNotNull(json);
    }

    @Test
    public void testKeyPairingSame() throws Exception {
        SignedVisualCode vc = KeyPairingVisualCodeTest.getCode();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(vc);
        assertEquals(json1, json2);
    }

    @Test
    public void testKeyPairingCycle() throws Exception {
        SignedVisualCode vc = KeyPairingVisualCodeTest.getCode();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(GSON.fromJson(GSON.toJson(vc), VisualCode.class));
        assertEquals(json1, json2);
    }

    // Delegate pairing visual code

    @Test
    public void testDelegatePairingSerialize() throws Exception {
        String json = GSON.toJson(DelegatePairingVisualCodeTest.getCode());
        assertNotNull(json);
    }

    @Test
    public void testDelegatePairingSame() throws Exception {
    	DelegatePairingVisualCode vc = DelegatePairingVisualCodeTest.getCode();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(vc);
        assertEquals(json1, json2);
    }

    @Test
    public void testDelegatePairingCycle() throws Exception {
    	DelegatePairingVisualCode vc = DelegatePairingVisualCodeTest.getCode();
        String json1 = GSON.toJson(vc);
        String json2 = GSON.toJson(GSON.fromJson(GSON.toJson(vc), VisualCode.class));
        assertEquals(json1, json2);
    }
}
