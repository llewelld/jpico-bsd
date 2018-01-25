package uk.ac.cam.cl.pico.crypto.messages;

import org.junit.Before;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.crypto.BrowserAuthToken;
import uk.ac.cam.cl.pico.crypto.Cookie;

@Deprecated
public class SessionDelegationMessageTest extends UnencryptedMessageTest {
    int sessionId;
    AuthToken authToken;
    SequenceNumber sequenceNumber;
    SessionDelegationMessage instance;
    ReauthState reauthState;
    int timeout;

    @Before
    public void setUp() throws MalformedURLException {
        sessionId = 1234567890;
        authToken =
                new BrowserAuthToken(Collections.<Cookie>emptyList(),
                        new URL("http://pico.cl.cam.ac.uk/login_url"),
                        new URL("http://pico.cl.cam.ac.uk/redirect_url"),
                           null);

        sequenceNumber = SequenceNumber.getRandomInstance();
        reauthState = ReauthState.CONTINUE;
        timeout = 1000;
        instance = new SessionDelegationMessage(sessionId, authToken, sequenceNumber, reauthState,
                timeout);;
    }

    @Override
    protected SessionDelegationMessage getInstance() {
        return instance;
    }
}
