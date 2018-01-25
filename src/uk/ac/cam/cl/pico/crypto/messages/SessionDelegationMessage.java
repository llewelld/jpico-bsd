/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import java.io.IOException;

import com.google.common.base.Objects;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataOutputStream;


/**
 * @deprecated protocol uses {@link StatusMessage} and {@link EncStatusMessage} instead now.
 */
@Deprecated
public final class SessionDelegationMessage extends
        UnencryptedMessage<EncSessionDelegationMessage> {

    private final AuthToken token;
    private final SequenceNumber sequenceNumber;
    private final ReauthState reauthState;
    private final int timeout;

    SessionDelegationMessage(int sessionId, AuthToken token, SequenceNumber sequenceNumber,
            ReauthState requestContinuous, int timeout) {
        super(sessionId);
        this.token = token;
        this.sequenceNumber = sequenceNumber;
        this.reauthState = requestContinuous;
        this.timeout = timeout;
    }

    public int getSessionId() {
        return sessionId;
    }

    public AuthToken getToken() {
        return token;
    }

    public SequenceNumber getSequenceNumber() {
        return sequenceNumber;
    }

    public ReauthState getReauthState() {
        return reauthState;
    }

    public int getTimeout() {
        return timeout;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SessionDelegationMessage) {
            SessionDelegationMessage other = (SessionDelegationMessage) obj;
            return (sessionId == other.sessionId); // TODO: expand.
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sessionId, token, sequenceNumber, reauthState, timeout);
    }
    
    @Override
    protected void writeDataToEncrypt(LengthPrependedDataOutputStream los) throws IOException {
        los.writeVariableLengthByteArray(sequenceNumber.toByteArray());
        los.writeVariableLengthByteArray(token.toByteArray());
        los.writeByte(reauthState.toByte());
        los.writeInt(timeout);
        los.flush();
    }

    @Override
    protected EncSessionDelegationMessage createEncryptedMessage(
            byte[] encryptedData, byte[] iv) {
        return new EncSessionDelegationMessage(sessionId, encryptedData, iv);
    }

    /**
     * Create a session delegation message, given the
     * 
     * @param sessionId Identifies the session to the service.
     * @param token the Session Authorisation Token that the pico can use to delegate the
     *        authenticated session to a different device.
     * @return
     */
    public static SessionDelegationMessage getInstance(
            int sessionId, AuthToken token, ReauthState reauthState, int timeout) {
        SequenceNumber challenge = SequenceNumber.getRandomInstance();
        return new SessionDelegationMessage(sessionId, token, challenge, reauthState, timeout);
    }
}
