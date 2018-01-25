/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.backup;

/**
 * Abstract class representing BackupKey exceptions.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 *
 */
public abstract class BackupKeyException extends Exception {

    private static final long serialVersionUID = 1L;

    public BackupKeyException() {
        super();
    }
    
    public BackupKeyException(final String description) {
        super(description);
    }
}