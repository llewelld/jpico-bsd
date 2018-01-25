/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.backup;

/**
 * Exception thrown when length of the BackupKey is invalid.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 *
 */
public class BackupFileDecryptionException extends BackupFileException {

    private static final long serialVersionUID = 1L;
    
    public BackupFileDecryptionException() {   
        super();
    }
    
    public BackupFileDecryptionException(final String description) {
        super(description);
    }
}