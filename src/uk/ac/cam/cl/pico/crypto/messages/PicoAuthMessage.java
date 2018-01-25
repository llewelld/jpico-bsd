/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import com.google.common.base.Objects;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;
import uk.ac.cam.cl.pico.crypto.Nonce;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataOutputStream;

/**
 * The third message of the SIGMA-I protocol, which is sent from the Pico to the service when the
 * Pico wants authenticate to the service using an existing account, or to create a new account.
 * <p>
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * @see uk.ac.cam.cl.pico.crypto.messages.EncPicoAuthMessage
 * 
 */
public final class PicoAuthMessage extends UnencryptedMessage<EncPicoAuthMessage> {

    private final PublicKey picoAccountIdentityPublicKey;
    private final byte[] signature;
    private final byte[] mac;
	private final byte[] extraData;

    PicoAuthMessage(
    		final int sessionId,
    		final PublicKey picoAccountIdentityPublicKey,
            final byte[] signature,
            final byte[] mac,
            final byte[] extraData) {
        super(sessionId);
        this.picoAccountIdentityPublicKey = picoAccountIdentityPublicKey;
        this.signature = signature;
        this.mac = mac;
        if (extraData == null) { 
        	this.extraData = new byte[0];
        } else {
        	this.extraData = extraData;
        }
    }

    public int getSessionId() {
        return sessionId;
    }

    public PublicKey getPicoAccountIdentityPublicKey() {
        return picoAccountIdentityPublicKey;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] getMac() {
        return mac;
    }
    
    public byte[] getExtraData() {
    	return extraData;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PicoAuthMessage) {
            PicoAuthMessage other = (PicoAuthMessage) obj;
            return (sessionId == other.sessionId)
                    && Arrays.equals(picoAccountIdentityPublicKey.getEncoded(),
                            other.picoAccountIdentityPublicKey.getEncoded())
                    && Arrays.equals(signature, other.signature)
                    && Arrays.equals(mac, other.mac)
                    && Arrays.equals(extraData, other.extraData);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sessionId, picoAccountIdentityPublicKey, signature,
                mac, extraData);
    }
    
    public static byte[] getBytesToSign(
            final Nonce serviceNonce,
            final int sessionId, 
            final PublicKey picoEphemeralPublicKey) {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	DataOutputStream dos = new DataOutputStream(baos);
    	try {
			dos.write(serviceNonce.getValue());
	    	dos.writeInt(sessionId);
	    	dos.write(picoEphemeralPublicKey.getEncoded());
		} catch (IOException e) {
			// Exception is re-thrown because a ByteArrayOutputStream specifically cannot raise
			// IOExceptions.
			throw new RuntimeException(e);
		}
    	return baos.toByteArray();
    }

    /**
     * 
     * @param serviceNonce
     * @param picoMacKey
     * @param picoEphemeralPublicKey
     * @return
     * @throws GeneralSecurityException
     * 
     * @deprecated removed to simplify message class implementation. Callers should implement
     *  verification checks themselves.
     */
    @Deprecated
    public boolean verify(final Nonce serviceNonce, final SecretKey picoMacKey,
            final PublicKey picoEphemeralPublicKey)
            throws GeneralSecurityException {
        checkNotNull(serviceNonce);
        checkNotNull(picoMacKey);
        checkNotNull(picoEphemeralPublicKey);

        // Check that the public key signature is correct
        final Signature signer = CryptoFactory.INSTANCE.sha256Ecdsa();
        signer.initVerify(picoAccountIdentityPublicKey);
        signer.update(getBytesToSign(serviceNonce, sessionId, picoEphemeralPublicKey));
        if (!signer.verify(this.signature)) {
            return false;
        }

        // Check that the symmetric key MAC is correct.
        final Mac macer = CryptoFactory.INSTANCE.sha256Hmac();
        macer.init(picoMacKey);
        if (!Arrays.equals(
                macer.doFinal(picoAccountIdentityPublicKey.getEncoded()), this.mac)) {
            return false;
        }

        // All the checks have passed.
        return true;
    }

    /**
     * Creates a PicoAuthMessage, using the provided keys to link the ephemeral keys to the Pico's
     * long term account identity key.
     * 
     * @param sessionId Identifies the session to the service.
     * @param serviceNonce
     * @param picoEphemeralPublicKey
     * @param picoIdKeyPair
     * @param picoMacKey is one of the derived keys from the DH Key agreement.
     * @return
     * @throws InvalidKeyException if one of the supplied keys is not valid.
     * @throws SignatureException if the signature cannot be created with the supplied arguments.
     */
    public static PicoAuthMessage getInstance(
    		final int sessionId,
    		final Nonce serviceNonce,
            final PublicKey picoEphemeralPublicKey,
            final KeyPair picoIdKeyPair,
            final SecretKey picoMacKey,
            final byte[] extraData) throws InvalidKeyException, SignatureException {
        // Get the bytes to be signed:
        // serviceNonce||sessionId||picoEphemeralPublicKey
        byte[] bytesToSign = getBytesToSign(serviceNonce, sessionId, picoEphemeralPublicKey);

        // Sign using the Pico's long-term private key:
        Signature signer = CryptoFactory.INSTANCE.sha256Ecdsa();
        signer.initSign(picoIdKeyPair.getPrivate());
        signer.update(bytesToSign);
        byte[] signature = signer.sign();

        // Make the MAC of the Pico's long-term public key, using the derived Pico MAC key:
        Mac macer = CryptoFactory.INSTANCE.sha256Hmac();
        macer.init(picoMacKey);
        byte[] mac = macer.doFinal(picoIdKeyPair.getPublic().getEncoded());

        return new PicoAuthMessage(
        		sessionId, picoIdKeyPair.getPublic(), signature, mac, extraData);
    }

    @Override
    protected EncPicoAuthMessage createEncryptedMessage(
    		final byte[] encryptedData, final byte[] iv) {
        return new EncPicoAuthMessage(sessionId, encryptedData, iv);
    }

    @Override
    protected void writeDataToEncrypt(LengthPrependedDataOutputStream los) throws IOException {
    	byte[] picoPublicKeyBytes = picoAccountIdentityPublicKey.getEncoded();
		los.writeVariableLengthByteArray(picoPublicKeyBytes);
		los.writeVariableLengthByteArray(signature);
		los.writeVariableLengthByteArray(mac);
		los.writeVariableLengthByteArray(extraData);
		los.flush();
    }
}
