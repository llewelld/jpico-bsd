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
public class BackupKeyInvalidException extends BackupKeyException {

    private static final long serialVersionUID = 1L;
    
    public BackupKeyInvalidException() {   
        super();
    }
    
    public BackupKeyInvalidException(final String description) {
        super(description);
    }
}