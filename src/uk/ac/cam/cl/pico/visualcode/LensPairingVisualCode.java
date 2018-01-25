/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

import com.google.gson.annotations.SerializedName;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import uk.ac.cam.cl.pico.Preconditions;

/**
 * Pico lens visual code for pairing. Contains the username/password credentials the Pico should
 * store.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
final public class LensPairingVisualCode extends LensVisualCode {
    
    public static String TYPE = "LP";
    
    public static LensPairingVisualCode getInstance(
    		URI serviceAddress,
    		byte[] serviceCommitment,
    		String serviceName,
    		Map<String, String> credentials) {
    	final LensPairingVisualCode code = new LensPairingVisualCode();
    	code.serviceAddress = Preconditions.checkNotNullOrEmpty(
    			serviceAddress, "serviceAddress cannot be null or empty");
    	code.serviceCommitment = Preconditions.checkNotNullOrEmpty(
    			serviceCommitment, "serviceCommitment cannot be null");
    	code.serviceName = Preconditions.checkNotNullOrEmpty(
    			serviceName, "serviceName cannot be null or empty");
    	code.credentials = checkNotNull(credentials, "credentials cannot be null");
    	return code;
    }

    @SerializedName("sn") private String serviceName;
    @SerializedName("c") private Map<String, String> credentials;
    
    // no-arg constructor for Gson
    private LensPairingVisualCode() {
    	super(TYPE);
    }

    /**
     * @return the name of the service identified by this visual code.
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return credentials of this visual code.
     */
    public Map<String, String> getCredentials() {
        return credentials;
    }
}
