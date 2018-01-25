/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import com.google.common.base.Objects;

import java.io.IOException;
import java.util.Arrays;

import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataOutputStream;

public final class StatusMessage extends UnencryptedMessage<EncStatusMessage> {
	
	public static final byte OK_DONE = (byte) 0;
	public static final byte OK_CONTINUE = (byte) 1;
	public static final byte REJECTED = (byte) -1;
	
	public static StatusMessage getRejectInstance(int sessionId) {
		return new StatusMessage(sessionId, REJECTED, null);
	}
	
	public static StatusMessage getDoneInstance(int sessionId, byte[] extraData) {
		return new StatusMessage(sessionId, OK_DONE, extraData);
	}
	
	public static StatusMessage getContinueInstance(int sessionId, byte[] extraData) {
		return new StatusMessage(sessionId, OK_CONTINUE, extraData);
	}

	private final byte status;
	private final byte[] extraData;
	
	StatusMessage(int sessionId, byte status, byte[] extraData) {
		super(sessionId);
		this.status = status;
		if (extraData == null) {
			this.extraData = new byte[0];
		} else {
			this.extraData = extraData;
		}
	}
	
	public byte getStatus() {
		return status;
	}
	
	public byte[] getExtraData() {
		return extraData;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StatusMessage) {
			StatusMessage other = (StatusMessage) obj;
			return (status == other.status) && Arrays.equals(extraData, other.extraData);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
	    return Objects.hashCode(sessionId, status, extraData);
	}
	
	@Override
	protected EncStatusMessage createEncryptedMessage(byte[] encryptedData, byte[] iv) {
		return new EncStatusMessage(sessionId, encryptedData, iv);
	}

	@Override
	protected void writeDataToEncrypt(LengthPrependedDataOutputStream los) throws IOException {
		los.writeByte(status);
		los.writeVariableLengthByteArray(extraData);
		los.flush();
	}

}
