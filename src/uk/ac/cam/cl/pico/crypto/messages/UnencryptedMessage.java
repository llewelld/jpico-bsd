/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;
import uk.ac.cam.cl.pico.crypto.util.LengthPrependedDataOutputStream;

/**
 * Abstract base class for the unencrypted forms of messages.
 * 
 * <p>All subclasses implement the <code>encrypt</code> method, which returns an instance of the
 * corresponding <code>{@link EncryptedMessage}</code> subclass. Starting with an
 * <code>UnencryptedMessage</code>, <code>m</code>, and a <code>SecretKey</code>, <code>k</code>,
 * the following will always evaluate to <code>true</code>:
 * 
 * <p><code>m.equals(m.encrypt(k).decrypt(k));</code>
 * 
 * @see EncryptedMessage
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
public abstract class UnencryptedMessage<E extends EncryptedMessage<?>>
        extends Message {

    protected final int sessionId;

    public UnencryptedMessage(final int sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Return an instance of the appropriate {@link EncryptedMessage} subclass which contains the
     * same data as this {@link UnencryptedMessage}, but with some fields encrypted under the key
     * <code>key</code>.
     * 
     * <p>The cipher for encryption is AES GCM.
     * 
     * @param key key to encrypt the hidden fields under.
     * @return instance of appropriate {@link EncryptedMessage} subclass.
     * @throws InvalidKeyException if <code>key</code> is not a valid AES GCM key.
     */
    public final E encrypt(SecretKey key) throws InvalidKeyException {
        Cipher cipher = CryptoFactory.INSTANCE.aes256();
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        // Get the IV of the cipher
        byte[] iv = cipher.getIV();

        // Write appropriate fields, formatted according to the concrete subclass, through the 
        // cipher using output stream framework. A CipherOutputStream is created and wrapped, then 
        // passed to the abstract writeMessageParts method, which subclasses implement to write the 
        // required fields.
        ByteArrayOutputStream bos = null;
        CipherOutputStream cos = null;
        LengthPrependedDataOutputStream dos = null;
        byte[] encryptedData;
    	try {
	        try {
	            bos = new ByteArrayOutputStream();
	            cos = new CipherOutputStream(bos, cipher);
	            dos = new LengthPrependedDataOutputStream(cos);
	            cos = null; // Set to null after wrapping.
	
	            // Write the fields to be encrypted
	            writeDataToEncrypt(dos);
	        } finally {
	        		if (dos != null) {
	        			dos.close();
	        		}
	        		if (cos != null) {
	        			cos.close();
	        		}
	        }
    	} catch (IOException e) {
    		// Should never happen. This is potentially dangerous, because the documentation of
    		// when a CipherOutputStream throws an IOException is not completely specified and 
    		// a subclass could throw IOExceptions from writeDataToEncrypt, which declares
    		// IOException essentially just to de-clutter code.
    		// TODO revise the current encrypt/decrypt mechanisms.
    		throw new RuntimeException(e);
    	}
        encryptedData = bos.toByteArray();
        // AES GCM is an authenticated encryption scheme, so it is not necessary to compute and
        // attach a separate MAC component to provide message authenticity/integrity.

        return createEncryptedMessage(encryptedData, iv);
    }

    /**
     * Concrete subclasses should override this method to specify how to create an instance of
     * their encrypted equivalent. They must use the <code>encryptedData</code> and
     * <code>iv</code>, but may also use other message member fields.
     * 
     * @param encryptedData encrypted message fields.
     * @param iv
     * @return encrypted equivalent of the concrete message subclass instance.
     */
    protected abstract E createEncryptedMessage(byte[] encryptedData, byte[] iv);

    /**
     * Concrete subclasses should override this method to specify how their hidden fields should be
     * packed before encryption.
     * 
     * @param dos output stream to write message parts to. Corresponding {@link EncryptedMessage}
     * 	subclass will read decrypted bytes from an input stream in the same order when re-creating
     * 	the unencrypted form of the message.
     */
    protected abstract void writeDataToEncrypt(LengthPrependedDataOutputStream dos)
    		throws IOException;

    @Override
    public abstract boolean equals(Object o);
}
