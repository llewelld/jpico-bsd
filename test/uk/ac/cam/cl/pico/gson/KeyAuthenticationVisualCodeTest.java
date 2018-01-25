package uk.ac.cam.cl.pico.gson;

import java.net.URI;

import uk.ac.cam.cl.pico.data.service.ServiceTest;
import uk.ac.cam.cl.pico.visualcode.KeyAuthenticationVisualCode;

public class KeyAuthenticationVisualCodeTest {

	public static KeyAuthenticationVisualCode getCode() {
		try {
            return KeyAuthenticationVisualCode.getInstance(
            		ServiceTest.ADDRESS,
            		ServiceTest.COMMITMENT, 
            		new URI("http://rendezvous.example.com/channel/example"),
            		"terminalCommitment".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception occured whilst creating visual code.", e);
        }
	}
}
