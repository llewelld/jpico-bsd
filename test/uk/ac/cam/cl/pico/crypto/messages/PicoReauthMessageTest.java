package uk.ac.cam.cl.pico.crypto.messages;



import org.junit.Before;

import java.net.MalformedURLException;

public class PicoReauthMessageTest extends UnencryptedMessageTest {
    int sessionId;
    SequenceNumber sequenceNumber;
    ReauthState reauthState;
    PicoReauthMessage instance;

    @Before
    public void setUp() throws MalformedURLException {
        sessionId = 1234567890;
        sequenceNumber = SequenceNumber.getRandomInstance();
        reauthState = ReauthState.CONTINUE;
        instance = new PicoReauthMessage(sessionId, reauthState, sequenceNumber);

    }

    @Override
    protected PicoReauthMessage getInstance() {
        return instance;
    }
}
