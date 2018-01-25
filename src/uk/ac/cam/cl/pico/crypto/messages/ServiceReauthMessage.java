/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import java.io.IOException;

import com.google.common.base.Objects;

import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataOutputStream;

public final class ServiceReauthMessage extends
        UnencryptedMessage<EncServiceReauthMessage> {

    private final SequenceNumber sequenceNumber;
    private final int timeout;
    private final ReauthState reauthState;

    public ServiceReauthMessage(final int sessionId, final ReauthState reauthState,
            final int timeout, final SequenceNumber sequenceNumber) {
        super(sessionId);
        this.reauthState = reauthState;
        this.timeout = timeout;
        this.sequenceNumber = sequenceNumber;
    }

    /* ************************** Accessor methods ************************** */

    public SequenceNumber getSequenceNumber() {
        return sequenceNumber;
    }

    public ReauthState getReauthState() {
        return reauthState;
    }

    public int getTimeout() {
        return timeout;
    }

    /* *********************** Serialisation Methods *********************** */

    @Override
    protected EncServiceReauthMessage createEncryptedMessage(
            byte[] encryptedData, byte[] iv) {
        return new EncServiceReauthMessage(sessionId, encryptedData, iv);
    }

    @Override
    protected void writeDataToEncrypt(LengthPrependedDataOutputStream los) throws IOException {
        los.write(reauthState.toByte());
        los.writeInt(timeout);
        los.writeVariableLengthByteArray(sequenceNumber.toByteArray());
        los.flush();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ServiceReauthMessage) {
            ServiceReauthMessage other = (ServiceReauthMessage) o;
            return sessionId == other.sessionId &&
                    reauthState == other.reauthState &&
                    timeout == other.timeout &&
                    sequenceNumber.equals(other.sequenceNumber);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sessionId, reauthState, timeout, sequenceNumber);
    }
}
