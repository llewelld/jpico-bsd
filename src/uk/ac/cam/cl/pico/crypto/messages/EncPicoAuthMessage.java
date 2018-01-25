/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataInputStream;

/**
 * Encrypted form of an {@link PicoAuthMessage}.
 * 
 * <p>An <code>EncAuthMessage</code> contains one cleartext item:
 * <ul>
 * <li><code>sessionId</code></li>
 * </ul>
 * 
 * <p>The encrypted items are encrypted using an AES cipher in Gallois counter mode, using the Pico's
 * symmetric session encryption key. The format of encrypted items is as follows:
 * 
 * <p><code>l||signature||m||mac</code>
 * 
 * <p>Where:
 * <ul>
 * <li><code>l</code> and <code>m</code> are four-byte (big-endian) integers specifying the length
 * (in bytes) of the next item.</li>
 * <li><code>signature</code> and <code>mac</code> are items of the corresponding unencrypted
 * {@link PicoAuthMessage}.</li>
 * </ul>
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * @see PicoAuthMessage
 * 
 */
public final class EncPicoAuthMessage extends EncryptedMessage<PicoAuthMessage> {

    EncPicoAuthMessage(int sessionId, byte[] encryptedData, byte[] iv) {
        super(sessionId, encryptedData, iv);
    }

    @Override
    protected PicoAuthMessage createUnencryptedMessage(LengthPrependedDataInputStream is)
    		throws IOException, FieldDeserializationException {
    	// Read raw bytes from input stream
    	byte[] picoPublicKeyBytes = is.readVariableLengthByteArray();
        byte[] signature = is.readVariableLengthByteArray();
        byte[] mac = is.readVariableLengthByteArray();
        byte[] extraData = is.readVariableLengthByteArray();

        KeyFactory kf = CryptoFactory.INSTANCE.ecKeyFactory();
        PublicKey picoPublicKey;
        try {
            picoPublicKey = kf.generatePublic(new X509EncodedKeySpec(picoPublicKeyBytes));
        } catch (InvalidKeySpecException e) {
        	// Thrown when the key bytes don't form a valid key spec -- the key cannot be 
        	// deserialized.
            throw new FieldDeserializationException(e);
        }

        return new PicoAuthMessage(sessionId, picoPublicKey, signature, mac, extraData);
    }
}
