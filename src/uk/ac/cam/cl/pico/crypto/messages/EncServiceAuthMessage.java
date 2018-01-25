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
import uk.ac.cam.cl.pico.crypto.Nonce;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataInputStream;

// TODO fix these comments in light of LengthPreprendedDataInputStream
/**
 * Encrypted form of a {@link ServiceAuthMessage}.
 * <p>
 * An <code>EncServiceAuthMessage</code> contains three cleartext items:
 * <ul>
 * <li><code>sessionId</code></li>
 * <li><code>serviceEphemeralPublicKey</code></li>
 * <li><code>serviceNonce</code></li>
 * </ul>
 * <p>
 * The <code>encryptedData</code> is encrypted using an AES cipher in Gallois counter mode, using
 * the service's symmetric session encryption key. The format of encrypted items is as follows:
 * <p>
 * <code>l||servicePublicKey||m||signature||n||mac</code>
 * <p>
 * Where:
 * <ul>
 * <li><code>l</code>, <code>m</code> and <code>n</code> are all four-byte (big-endian) integers
 * specifying the length (in bytes) of the next item.</li>
 * <li><code>servicePublicKey</code> is the long term public key of the Pico encoded in the X.509
 * binary encoding format.</li>
 * <li><code>signature</code> and <code>mac</code> are items of the corresponding unencrypted
 * {@link ServiceAuthMessage}.</li>
 * </ul>
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * @see uk.ac.cam.cl.pico.crypto.messages.ServiceAuthMessage
 * 
 */
public final class EncServiceAuthMessage extends EncryptedMessage<ServiceAuthMessage> {

    private final PublicKey serviceEphemPublicKey;
    private final Nonce serviceNonce;

    EncServiceAuthMessage(int sessionId, PublicKey serviceEphemeralPublicKey,
            Nonce serviceNonce, byte[] encryptedData, byte[] iv) {
        super(sessionId, encryptedData, iv);
        this.serviceEphemPublicKey = serviceEphemeralPublicKey;
        this.serviceNonce = serviceNonce;
    }

    public PublicKey getServiceEphemeralPublicKey() {
        return serviceEphemPublicKey;
    }

    public Nonce getServiceNonce() {
        return serviceNonce;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EncServiceAuthMessage) {
            EncServiceAuthMessage other = (EncServiceAuthMessage) obj;
            return (serviceEphemPublicKey
                    .equals(other.serviceEphemPublicKey))
                    && (serviceNonce.equals(other.serviceNonce))
                    && super.equals(other);
        } else {
            return false;
        }
    }

    @Override
    protected ServiceAuthMessage createUnencryptedMessage(
    		LengthPrependedDataInputStream dis) throws IOException, FieldDeserializationException {
        // Read the following fields: servicePublicKey||signature||mac
        byte[] servicePublicKeyBytes = dis.readVariableLengthByteArray();
        byte[] signature = dis.readVariableLengthByteArray();
        byte[] mac = dis.readVariableLengthByteArray();

        // Create the PublicKey object from the encoded bytes
        KeyFactory kf = CryptoFactory.INSTANCE.ecKeyFactory();
        PublicKey servicePublicKey;
        try {
            servicePublicKey = kf.generatePublic(new X509EncodedKeySpec(servicePublicKeyBytes));
        } catch (InvalidKeySpecException e) {
            throw new EncryptedMessage.FieldDeserializationException(e);
        }

        return new ServiceAuthMessage(
        		sessionId, serviceEphemPublicKey, serviceNonce, servicePublicKey, signature, mac);
    }
}
