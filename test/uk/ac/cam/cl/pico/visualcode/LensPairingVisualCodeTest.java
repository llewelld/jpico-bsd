package uk.ac.cam.cl.pico.visualcode;

import java.util.HashMap;
import java.util.Map;

import uk.ac.cam.cl.pico.data.service.ServiceTest;

public class LensPairingVisualCodeTest {
	
	public static final Map<String, String> NO_CREDENTIALS = new HashMap<String, String>();
	public static final Map<String, String> SOME_CREDENTIALS;
	
	static {
		SOME_CREDENTIALS = new HashMap<String, String>();
		SOME_CREDENTIALS.put("key1", "value1");
		SOME_CREDENTIALS.put("key2", "value2");
	}
	
	public static LensPairingVisualCode getCode(Map<String, String> credentials) {
        try {
            return LensPairingVisualCode.getInstance(
            		ServiceTest.ADDRESS,
            		ServiceTest.COMMITMENT,
            		ServiceTest.NAME,
            		credentials);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception occured whilst creating visual code.", e);
        }
    }
	
	public static LensPairingVisualCode getCodeNone() {
		return getCode(NO_CREDENTIALS);
	}
	
	public static LensPairingVisualCode getCodeSome() {
		return getCode(SOME_CREDENTIALS);
	}
}
