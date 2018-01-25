/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import java.io.IOException;

import uk.ac.cam.cl.pico.crypto.messages.ReauthState.InvalidReauthStateIndexException;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataInputStream;

public final class EncServiceReauthMessage extends
        EncryptedMessage<ServiceReauthMessage> {

    public EncServiceReauthMessage(int sessionId, byte[] encryptedData, byte[] iv) {
        super(sessionId, encryptedData, iv);
    }

    @Override
    protected ServiceReauthMessage createUnencryptedMessage(
            LengthPrependedDataInputStream dis) throws IOException {
        final ReauthState type;
        try {
            type = ReauthState.fromByte(dis.readByte());
        } catch (InvalidReauthStateIndexException e) {
            throw new IOException(e);
        }
        final int timeout = dis.readInt();
        final SequenceNumber challenge =
                SequenceNumber.fromByteArray(dis.readVariableLengthByteArray());
        return new ServiceReauthMessage(sessionId, type, timeout, challenge);
    }

}
