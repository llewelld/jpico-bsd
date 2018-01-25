/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.backup;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;
import uk.ac.cam.cl.pico.crypto.CryptoRuntimeException;

/**
 * Abstraction representing a backup of the Pico database.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 *
 */
public final class BackupFile {
    
    private final File dbFile;    
    
    private BackupFile(final File dbFile) {
        this.dbFile = dbFile;
    }   
    
    public static BackupFile newInstance(final File dbFile) {
        // Verify the method's preconditions
        checkNotNull(dbFile);
        
        return new BackupFile(dbFile);
    }  
    
    /**
     * Create an encrypted backup of the Pico pairings and services database.
     * @param backupKey
     * @return Encrypted backup file.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public EncBackupFile createEncBackupFile(final BackupKey backupKey)
            throws FileNotFoundException, IOException {
        // Verify the method's preconditions
        checkNotNull(backupKey);
        
        try {
            final FileInputStream fileIs = new FileInputStream(dbFile); 
            byte[] plaintext = new byte[(int) dbFile.length()];
            try {
                fileIs.read(plaintext);
                fileIs.close();
                
                final Cipher cipher = CryptoFactory.INSTANCE.aes256();
                cipher.init(Cipher.ENCRYPT_MODE, backupKey.getSecretKey());
                final byte[] ciphertext = cipher.doFinal(plaintext);
                
                final ByteArrayInputStream cipherIs = new ByteArrayInputStream(ciphertext);
                final byte[] encryptedData = new byte[cipherIs.available()];
                cipherIs.read(encryptedData);

                return new EncBackupFile(encryptedData, cipher.getIV());
            } finally {
                fileIs.close();
            }
        } catch (InvalidKeyException e) {
            throw new CryptoRuntimeException();
        } catch (BadPaddingException e) {
            throw new CryptoRuntimeException();
        } catch (IllegalBlockSizeException e) {
            throw new CryptoRuntimeException();
        }
    }  
    
    public File getDbFile() {
        return dbFile;        
    }
}