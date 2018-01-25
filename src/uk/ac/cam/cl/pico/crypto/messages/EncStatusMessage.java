/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import java.io.IOException;

import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataInputStream;

public class EncStatusMessage extends EncryptedMessage<StatusMessage> {

	public EncStatusMessage(int sessionId, byte[] encryptedData, byte[] iv) {
		super(sessionId, encryptedData, iv);
	}

	@Override
	protected StatusMessage createUnencryptedMessage(LengthPrependedDataInputStream is)
			throws IOException {
		return new StatusMessage(sessionId, is.readByte(), is.readVariableLengthByteArray());
	}

}
