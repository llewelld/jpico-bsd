/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.backup;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.ByteStreams.copy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;

/**
 * Abstraction representing an encrypted backup of the Pico database.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 *
 */
public final class EncBackupFile {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
            EncBackupFile.class.getSimpleName());
    
    private final byte[] encryptedData;
    private final byte[] iv;
    
    public EncBackupFile(final byte[] encryptedData, final byte[] iv) {
        this.encryptedData = encryptedData;
        this.iv = iv;
    }

    public BackupFile createUnencryptedBackupFile(final File dbFile, final BackupKey backupKey)
            throws FileNotFoundException, IOException, BackupKeyInvalidException,
            BackupFileDecryptionException {
        // Verify the method's preconditions
        checkNotNull(dbFile);
        checkNotNull(backupKey);
                                            
        final Cipher cipher = CryptoFactory.INSTANCE.aes256();
        final IvParameterSpec ips = new IvParameterSpec(iv);
        
        try {
            cipher.init(Cipher.DECRYPT_MODE, backupKey.getSecretKey(), ips);
            final byte[] decryptedData = cipher.doFinal(encryptedData);
            final ByteArrayInputStream cipherIs = new ByteArrayInputStream(decryptedData);
            try {
                if(!dbFile.exists()) {
                    dbFile.getParentFile().mkdirs();
                    dbFile.createNewFile();
                } 
                
                final FileOutputStream outputStream = new FileOutputStream(dbFile);
                try {
                    // Guava ByteStreams.copy() does not flush or close either stream
                    copy(cipherIs, outputStream);
                    outputStream.flush();
                    return BackupFile.newInstance(dbFile);
                } finally {
                    outputStream.close();
                }
            } finally {
                cipherIs.close();
            }
        } catch (InvalidAlgorithmParameterException e) {
            LOGGER.error("Failure decrypting Pico database backup", e); 
            throw new BackupFileDecryptionException();
        } catch (IllegalBlockSizeException e) {
            // Decryption failure
            LOGGER.error("Failure decrypting Pico database", e); 
            throw new BackupFileDecryptionException();
        } catch (BadPaddingException e) {
             // BadPaddingException (authenticated encryption MAC failure)
             LOGGER.error("Authenticated encryption failure", e); 
             throw new BackupFileDecryptionException();
        } catch (InvalidKeyException e) {
            // Backup key is invalid
            LOGGER.error("Backup key is invalid", e);
            throw new BackupKeyInvalidException("BackupKey is invalid");
        }
    }
    
    /**
     * Accessor method for the instance's encryptedData attribute.
     * @return The encrypted data.
     */
    public byte[] getEncryptedData() {
        return encryptedData;
    }
    
    /**
     * Accessor method for the instance's iv attribute.
     * @return The initialization vector (IV) used to encrypt the backup file.
     */
    public byte[] getIv() {
        return iv;
    }
}