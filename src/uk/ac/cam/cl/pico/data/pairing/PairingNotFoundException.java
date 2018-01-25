/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

/**
 * Pairing not found in data store.
 * 
 * @author Graeme Jenkinson <gcj21@cam.ac.uk>
 * 
 */
public class PairingNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public PairingNotFoundException() {
        super();
    }

    public PairingNotFoundException(final String message) {
        super(message);
    }
}
