/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.backup;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;

/**
 * Abstraction representing the key used to encrypt a backup of the Pico pairings and
 * services database.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 *
 */
public abstract class BackupKey {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
            BackupKey.class.getSimpleName());
    private static final int BACKUP_USER_SECRET_LENGTH = 12;
    
	protected final byte[] userSecret = new byte[BACKUP_USER_SECRET_LENGTH];
	private final SecretKey secretKey;	

    // Generate a new random BackupKey key    
	protected BackupKey() {
        // Generate the random 96 bytes to be remember by the user,
    	// this is used to generate the 128-bit AES key
        final SecureRandom random = new SecureRandom();
        random.nextBytes(userSecret);
         
        final MessageDigest urkbImage = CryptoFactory.INSTANCE.sha256();  
        urkbImage.update(userSecret);              
        secretKey = new SecretKeySpec(urkbImage.digest(), "AES");
	}

    // Generate a AES key from the userSecret 
	protected BackupKey(final byte[] userSecret) throws BackupKeyInvalidLengthException {	
	    // Verify the method's preconditions
	    checkNotNull(userSecret, "userSecret cannot be null");
	    
	    if (userSecret.length != BACKUP_USER_SECRET_LENGTH) {
	        LOGGER.error("Length of user secret ({}) is != {}",
	                userSecret.length, BACKUP_USER_SECRET_LENGTH);
	        throw new BackupKeyInvalidLengthException();
	    }
	    
	    // Store the userSecret (the preimage)
		System.arraycopy(userSecret, 0, this.userSecret, 0, BACKUP_USER_SECRET_LENGTH);

		// Generate a key, by hashing the userSecret (preimage)
        final MessageDigest urkbImage = CryptoFactory.INSTANCE.sha256();  
        urkbImage.update(this.userSecret);              
        secretKey = new SecretKeySpec(urkbImage.digest(), "AES");
	}		
		
	public static boolean isValid(final byte[] userSecret) {
	    if (userSecret.length != BACKUP_USER_SECRET_LENGTH) {
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Accessor method for the BackupKey instances userSecret.
	 * @return The userSecret (as a byte[]).
	 */
	public byte[] getUserSecret() {
		return userSecret;
	}
	
	/**
	 * Accessor method for the BackupKey instances secretKey.
	 * @return The SecretKey instance.
	 */
	public SecretKey getSecretKey() {
		return secretKey;
	}	
}