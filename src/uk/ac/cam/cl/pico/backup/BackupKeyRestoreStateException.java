/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.backup;

/**
 * Exception thrown when length of the BackupKey is in an invalid state to restore.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 *
 */
public class BackupKeyRestoreStateException extends BackupKeyException {

    private static final long serialVersionUID = 1L;
    
    public BackupKeyRestoreStateException() {   
        super();
    }
    
    public BackupKeyRestoreStateException(final String description) {
        super(description);
    }
}