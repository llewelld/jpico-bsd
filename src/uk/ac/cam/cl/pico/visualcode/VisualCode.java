/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

import com.google.gson.annotations.SerializedName;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract base class for all visual codes. All visual codes have a type and the commitment
 * provided by that service which will be checked when the Pico authenticates to it (see
 * {@link #getServiceCommitment}). For example this commitment may be the hash of the service's long
 * term public key.
 * 
 * <p> Subclasses can extend this class to add additional information to the 
 * visual code for a specific purpose.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
public abstract class VisualCode {

    /**
     * Identifies the type of visual code, this is a work around for GSON not handling class
     * hierarchies.
     */
    @SerializedName("t") protected String type;

    protected VisualCode(final String type) {
        this.type = checkNotNull(type);
    }
    
    public String getType() {
    	return type;
    }
}
