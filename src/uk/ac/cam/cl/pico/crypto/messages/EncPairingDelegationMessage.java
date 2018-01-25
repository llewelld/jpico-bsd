package uk.ac.cam.cl.pico.crypto.messages;

import java.io.IOException;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.crypto.AuthTokenFactory;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataInputStream;

public final class EncPairingDelegationMessage extends
		EncryptedMessage<PairingDelegationMessage> {
	
	public EncPairingDelegationMessage(int sessionId, byte[] encryptedData, byte[] iv) {
		super(sessionId, encryptedData, iv);
	}

	@Override
	protected PairingDelegationMessage createUnencryptedMessage(
			LengthPrependedDataInputStream dis)
			throws IOException,
			uk.ac.cam.cl.pico.crypto.messages.EncryptedMessage.FieldDeserializationException {
		final SequenceNumber sequenceNumber = SequenceNumber.fromByteArray(dis.readVariableLengthByteArray());
		final String serviceName = new String(dis.readVariableLengthByteArray());
		final AuthToken token = AuthTokenFactory.fromByteArray(dis.readVariableLengthByteArray());
		final byte[] commitment = dis.readVariableLengthByteArray();
		final String address = new String(dis.readVariableLengthByteArray());
		final byte[] extraData = dis.readVariableLengthByteArray();
		
		return new PairingDelegationMessage(sessionId, sequenceNumber, serviceName, token, commitment, address, extraData);
	}

}
