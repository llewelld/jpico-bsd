/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import com.google.common.base.Objects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataInputStream;

/**
 * Abstract base class for the encrypted forms of messages.
 * 
 * <p>All subclasses implement the <code>decrypt</code> method, which returns an instance of the
 * corresponding {@link UnencryptedMessage} subclass.
 * 
 * @see UnencryptedMessage
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
public abstract class EncryptedMessage<U extends UnencryptedMessage<?>>
        extends Message {
	
	public static class FieldDeserializationException extends Exception {

		private static final long serialVersionUID = -2251777007578448888L;

		public FieldDeserializationException() {
			super();
		}

		public FieldDeserializationException(String message, Throwable cause) {
			super(message, cause);
		}

		public FieldDeserializationException(String message) {
			super(message);
		}

		public FieldDeserializationException(Throwable cause) {
			super(cause);
		}
		
	}

    protected final int sessionId;
    protected final byte[] encryptedData;
    protected final byte[] iv;

    public EncryptedMessage(int sessionId, byte[] encryptedData, byte[] iv) {
        this.sessionId = sessionId;
        this.encryptedData = encryptedData;
        this.iv = iv;
    }

    public final int getSessionId() {
        return sessionId;
    }    
    
    public final U decrypt(SecretKey key) 
    		throws InvalidKeyException, InvalidAlgorithmParameterException, 
    		IllegalBlockSizeException, BadPaddingException, FieldDeserializationException {
        Cipher cipher = CryptoFactory.INSTANCE.aes256();
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        // Initialise cipher with IV and encryption key
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        final byte[] decryptedData = cipher.doFinal(encryptedData);

        try {
	        LengthPrependedDataInputStream dis = null;
	        try {
	            dis = new LengthPrependedDataInputStream(new ByteArrayInputStream(decryptedData));
	            return createUnencryptedMessage(dis);
	        } finally {
	    		if (dis != null) {
	    			dis.close();
	    		}
	        }
        } catch (IOException e) {
        	// Re-thrown unchecked because ByteArrayInputStream should never raise any IOExceptions
        	throw new RuntimeException(e);
        }
    }

    protected abstract U createUnencryptedMessage(LengthPrependedDataInputStream is)
    		throws IOException, FieldDeserializationException;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EncryptedMessage<?>) {
            EncryptedMessage<?> other = (EncryptedMessage<?>) obj;
            return (getClass() == other.getClass() &&
                    sessionId == other.sessionId &&
                    Arrays.equals(encryptedData, other.encryptedData)) &&
                    Arrays.equals(iv, other.iv);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(sessionId, encryptedData, iv);
    }
}
