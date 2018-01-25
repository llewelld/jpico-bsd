/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

import com.google.gson.annotations.SerializedName;

import java.net.URI;

import uk.ac.cam.cl.pico.Preconditions;

/**
 * VisualCode containing details to authenticate with a service.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 */
final public class KeyAuthenticationVisualCode extends KeyVisualCode {

    public static String TYPE = "KA";
    
    public static KeyAuthenticationVisualCode getInstance(
            final URI serviceAddress,
            final byte[] serviceCommitment,
            final URI terminalAddress,
            final byte[] terminalCommitment) {
        return getInstance(serviceAddress, serviceCommitment, terminalAddress, terminalCommitment, null);
    }
    
    public static KeyAuthenticationVisualCode getInstance(
            final URI serviceAddress,
            final byte[] serviceCommitment,
            final URI terminalAddress,
            final byte[] terminalCommitment,
            final byte[] extraData) {
    	final KeyAuthenticationVisualCode code = new KeyAuthenticationVisualCode();
    	code.serviceAddress = Preconditions.checkNotNullOrEmpty(
        		serviceAddress, "serviceAddress cannot be null or empty");
        code.serviceCommitment = Preconditions.checkNotNullOrEmpty(
        		serviceCommitment, "serviceCommitment cannot be null or empty");
        code.extraData = extraData;
        code.terminal = TerminalDetails.getInstance(terminalAddress, terminalCommitment);
        return code;
    }

    public static KeyAuthenticationVisualCode getInstanceNoTerminal(final URI serviceAddress,
    		final byte[] serviceCommitment){
    	return getInstanceNoTerminal(serviceAddress, serviceCommitment, null);
    }


    public static KeyAuthenticationVisualCode getInstanceNoTerminal(
    		final URI serviceAddress,
    		final byte[] serviceCommitment,
            final byte[] extraData) {
    	final KeyAuthenticationVisualCode code = new KeyAuthenticationVisualCode();
    	code.serviceAddress = Preconditions.checkNotNullOrEmpty(
        		serviceAddress, "serviceAddress cannot be null or empty");
        code.serviceCommitment = Preconditions.checkNotNullOrEmpty(
        		serviceCommitment, "serviceCommitment cannot be null or empty");
        code.terminal = TerminalDetails.getEmptyInstance();
        code.extraData = extraData;
        return code;
    }

    @SerializedName("sc") private byte[] serviceCommitment;
    @SerializedName("ed") private byte[] extraData;
    
    private KeyAuthenticationVisualCode() {
        super(TYPE);
    }

    public byte[] getServiceCommitment() {
        return serviceCommitment;
    }
    
    public byte[] getExtraData() {
        return extraData;
    }
}
