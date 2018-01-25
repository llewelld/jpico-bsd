/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import java.io.IOException;

import uk.ac.cam.cl.pico.crypto.messages.ReauthState.InvalidReauthStateIndexException;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataInputStream;

public final class EncPicoReauthMessage extends
        EncryptedMessage<PicoReauthMessage> {

    public EncPicoReauthMessage(int sessionId, byte[] encryptedData, byte[] iv) {
        super(sessionId, encryptedData, iv);
    }

    @Override
    protected PicoReauthMessage createUnencryptedMessage(
            final LengthPrependedDataInputStream dis) throws IOException {
        final ReauthState reauthState;
        try {
            reauthState = ReauthState.fromByte(dis.readByte());
        } catch (InvalidReauthStateIndexException e) {
            throw new IOException(e);
        }
        final SequenceNumber sequenceNumber =
                SequenceNumber.fromByteArray(dis.readVariableLengthByteArray());
        final byte[] extraData = dis.readVariableLengthByteArray();
        return new PicoReauthMessage(sessionId, reauthState, sequenceNumber, extraData);
    }
}