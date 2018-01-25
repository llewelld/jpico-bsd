package uk.ac.cam.cl.pico.crypto.messages;

import java.io.IOException;
import java.util.Arrays;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataOutputStream;

import com.google.common.base.Objects;

/**
 * @author David Llewellyn-Jones <David.Llewellyn-Jones@cl.cam.ac.uk>
 * 
 */
public final class PairingDelegationMessage extends
		UnencryptedMessage<EncPairingDelegationMessage> {

	private final SequenceNumber sequenceNumber;
	private final String serviceName;
	private final AuthToken token;

	private final byte[] commitment;
	private final String address;

	private final byte[] extraData;

	public PairingDelegationMessage(final int sessionId,
			final SequenceNumber sequenceNumber, 
			final String serviceName,
			final AuthToken token,
			final byte[] commitment,
			final String address,
			final byte[] extraData) {
		super(sessionId);

		this.sequenceNumber = sequenceNumber;
		this.serviceName = serviceName;
		this.token = token;
		this.commitment = commitment;
		this.address = address;
		this.extraData = extraData;
	}

	/* ************************** Accessor methods ************************** */

	public SequenceNumber getSequenceNumber() {
		return sequenceNumber;
	}

	public String getServiceName() {
		return serviceName;
	}

	public AuthToken getAuthToken() {
		return token;
	}
	
	public byte[] getCommitment() {
		return commitment;
	}
	
	public String getAddress() {
		return address;
	}

	public byte[] getExtraData() {
		return extraData;
	}

	/* *********************** Serialisation Methods *********************** */
	@Override
	protected EncPairingDelegationMessage createEncryptedMessage(
			final byte[] encryptedData, final byte[] iv) {
		return new EncPairingDelegationMessage(sessionId, encryptedData, iv);
	}

	@Override
	protected void writeDataToEncrypt(final LengthPrependedDataOutputStream los)
			throws IOException {
		los.writeVariableLengthByteArray(sequenceNumber.toByteArray());
		los.writeVariableLengthByteArray(serviceName.getBytes());
		los.writeVariableLengthByteArray(token.toByteArray());
		los.writeVariableLengthByteArray(commitment);
		los.writeVariableLengthByteArray(address.toString().getBytes());
		los.writeVariableLengthByteArray(extraData);
		los.flush();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PairingDelegationMessage) {
			PairingDelegationMessage other = (PairingDelegationMessage) o;
			return sessionId == other.sessionId
					&& sequenceNumber.equals(other.sequenceNumber)
					&& serviceName.equals(other.serviceName)
					&& token.equals(other.token)
					&& Arrays.equals(commitment, other.commitment)
					&& address.equals(other.address)
					&& Arrays.equals(extraData, other.extraData);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(sessionId, sequenceNumber, serviceName, token,
				commitment, address, extraData);
	}

}
