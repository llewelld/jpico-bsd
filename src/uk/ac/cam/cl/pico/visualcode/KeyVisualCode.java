/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

import com.google.gson.annotations.SerializedName;

import java.net.URI;

/**
 * Abstract class representing a VisualCode containing a key.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 */
public abstract class KeyVisualCode extends VisualCode implements WithTerminalDetails {
	
	@SerializedName("sa") protected URI serviceAddress;
	@SerializedName("td") protected TerminalDetails terminal;

    protected KeyVisualCode(final String type) {
        super(type);
    }
    
    public URI getServiceAddress() {
    	return serviceAddress;
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
