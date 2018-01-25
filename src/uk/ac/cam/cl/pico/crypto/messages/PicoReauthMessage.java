/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import java.io.IOException;
import java.util.Arrays;

import com.google.common.base.Objects;

import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataOutputStream;

public final class PicoReauthMessage extends
        UnencryptedMessage<EncPicoReauthMessage> {

    private final SequenceNumber sequenceNumber;
    private final ReauthState reauthState;
    private final byte[] extraData;
    
    public PicoReauthMessage(final int sessionId, final ReauthState reauthState,
            final SequenceNumber sequenceNumber) {
        this(sessionId, reauthState, sequenceNumber, new byte[0]);
    }
    
    public PicoReauthMessage(final int sessionId, final ReauthState reauthState,
            final SequenceNumber sequenceNumber, final byte[] extraData) {
        super(sessionId);
        this.reauthState = reauthState;
        this.sequenceNumber = sequenceNumber;
        this.extraData = extraData;
    }

    /* ************************** Accessor methods ************************** */

    public SequenceNumber getSequenceNumber() {
        return sequenceNumber;
    }

    public ReauthState getReauthState() {
        return reauthState;
    }

    public byte[] getExtraData() {
        return extraData;
    }
    
    /* *********************** Serialisation Methods *********************** */

    @Override
    protected EncPicoReauthMessage createEncryptedMessage(
            final byte[] encryptedData, final byte[] iv) {
        return new EncPicoReauthMessage(sessionId, encryptedData, iv);
    }

    @Override
    protected void writeDataToEncrypt(final LengthPrependedDataOutputStream los)
            throws IOException {
        los.write(reauthState.toByte());
        los.writeVariableLengthByteArray(sequenceNumber.toByteArray());
        los.writeVariableLengthByteArray(extraData);
        los.flush();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PicoReauthMessage) {
            PicoReauthMessage other = (PicoReauthMessage) o;
            return sessionId == other.sessionId &&
                    reauthState == other.reauthState &&
                    sequenceNumber.equals(other.sequenceNumber) &&
                    Arrays.equals(extraData, other.extraData);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(sessionId, reauthState, sequenceNumber, extraData);
    }
}