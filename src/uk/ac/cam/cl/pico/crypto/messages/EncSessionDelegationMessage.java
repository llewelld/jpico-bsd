/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import java.io.IOException;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.crypto.AuthTokenFactory;
import uk.ac.cam.cl.pico.crypto.messages.ReauthState.InvalidReauthStateIndexException;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataInputStream;

/**
 * @deprecated protocol uses {@link StatusMessage} and {@link EncStatusMessage} instead now.
 */
@Deprecated
public final class EncSessionDelegationMessage extends
        EncryptedMessage<SessionDelegationMessage> {

    EncSessionDelegationMessage(int sessionId, byte[] encryptedData, byte[] iv) {
        super(sessionId, encryptedData, iv);
    }

    @Override
    protected SessionDelegationMessage createUnencryptedMessage(
            LengthPrependedDataInputStream dis) throws IOException {
        SequenceNumber challenge = SequenceNumber.fromByteArray(dis.readVariableLengthByteArray());
        AuthToken t = AuthTokenFactory.fromByteArray(dis.readVariableLengthByteArray());
        ReauthState reauthState;
        try {
            reauthState = ReauthState.fromByte(dis.readByte());
        } catch (InvalidReauthStateIndexException e) {
            throw new IOException(e);
        }
        int timeout = dis.readInt();
        return new SessionDelegationMessage(sessionId, t, challenge, reauthState, timeout);
    }
}
