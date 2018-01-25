/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

import java.net.URI;

import com.google.gson.annotations.SerializedName;


/**
 * Pico lens visual code for authentication.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
final public class LensAuthenticationVisualCode extends LensVisualCode implements WithTerminalDetails {

    public static String TYPE = "LA";
        
    public static LensAuthenticationVisualCode getInstance(
    		URI terminalAddress,
    		byte[] terminalCommitment) {
        final LensAuthenticationVisualCode code = new LensAuthenticationVisualCode();
        code.terminal = TerminalDetails.getInstance(terminalAddress, terminalCommitment);
        return code;
    }
    
    public static LensAuthenticationVisualCode getInstanceNoTerminal() {
        final LensAuthenticationVisualCode code = new LensAuthenticationVisualCode();
        code.terminal = TerminalDetails.getEmptyInstance();
        return code;
    }
	
	@SerializedName("td")  private TerminalDetails terminal;
	
	// no-arg constructor for Gson
	private LensAuthenticationVisualCode() {
		super(TYPE);
	}

	@Override
	public URI getTerminalAddress() {
		return terminal.getTerminalAddress();
	}

	@Override
	public byte[] getTerminalCommitment() {
		return terminal.getTerminalCommitment();
	}

	@Override
	public boolean hasTerminal() {
		return (terminal != null && terminal.hasTerminal());
	}
}
