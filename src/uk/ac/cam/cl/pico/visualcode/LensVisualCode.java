/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

import com.google.gson.annotations.SerializedName;

import java.net.URI;

/**
 * Abstract base class for visual codes used by the Pico Lens.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 */
public abstract class LensVisualCode extends VisualCode {

    @SerializedName("sa") protected URI serviceAddress;
    @SerializedName("sc") protected byte[] serviceCommitment;
    
    // For subclasses with no-arg constructors to call.
    protected LensVisualCode(String type) {
    	super(type);
    }

    /**
     * @return the address of the service identified by this visual code.
     */
    public URI getServiceAddress() {
        return serviceAddress;
    }

    public byte[] getServiceCommitment() {
        return serviceCommitment;
    }
}
