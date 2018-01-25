package uk.ac.cam.cl.pico.crypto.messages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cl.pico.crypto.messages.ReauthState.InvalidReauthStateIndexException;

public class ReauthStateTest {


    @Test
    public void testByteArrayRoundTrip() throws InvalidReauthStateIndexException {
        for (ReauthState s : ReauthState.values()) {
            assertEquals(s, ReauthState.fromByte(s.toByte()));
        }
    }

    @Test(expected = InvalidReauthStateIndexException.class)
    public void testInvalidByteArray() throws InvalidReauthStateIndexException {
        byte x = -10;
        ReauthState.fromByte(x);
    }

}
