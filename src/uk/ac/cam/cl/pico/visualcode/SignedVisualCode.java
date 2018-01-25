/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

public interface SignedVisualCode {

    /**
     * @return the signature of this visual code.
     */
    byte[] getSignature();
    
    /**
     * @return the bytes that should be signed for this visual code.
     */
    byte[] getBytesToSign();

}
