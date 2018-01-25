package uk.ac.cam.cl.pico.crypto.messages;



import org.junit.Before;

import java.net.MalformedURLException;

public class ServiceReauthMessageTest extends UnencryptedMessageTest {
    int sessionId;
    SequenceNumber sequenceNumber;
    ReauthState reauthState;
    ServiceReauthMessage instance;
    int timeout;

    @Before
    public void setUp() throws MalformedURLException {
        sessionId = 1234567890;
        sequenceNumber = SequenceNumber.getRandomInstance();
        reauthState = ReauthState.CONTINUE;
        timeout = 10000;
        instance = new ServiceReauthMessage(sessionId, reauthState, timeout, sequenceNumber);
    }

    @Override
    protected ServiceReauthMessage getInstance() {
        return instance;
    }
}
