package uk.ac.cam.cl.pico.visualcode;

import java.net.URI;
import java.security.PublicKey;

import uk.ac.cam.cl.pico.Preconditions;
import uk.ac.cam.cl.pico.crypto.HashUtils;
import uk.ac.cam.cl.pico.crypto.Nonce;

import com.google.gson.annotations.SerializedName;

/**
 * Lens visual code for delegating authority
 * 
 * @author David Llewellyn-Jones <David.Llewellyn-Jones@cl.cam.ac.uk>
 *
 */
public class DelegatePairingVisualCode extends VisualCode implements WithTerminalDetails { 
    
    public static String TYPE = "DP";
    
	@SerializedName("tn") private String terminalName;
	@SerializedName("n") private Nonce nonce;
	@SerializedName("td") private TerminalDetails terminal;
	
    // no-arg constructor for Gson
    private DelegatePairingVisualCode() {
    	super(TYPE);
    }

    public static DelegatePairingVisualCode getInstance(
    		String terminalName,
    		Nonce nonce,
    		URI terminalAddress,
    		PublicKey terminalPublicKey) {
    	final DelegatePairingVisualCode code = new DelegatePairingVisualCode();
    	code.terminalName = Preconditions.checkNotNullOrEmpty(
    			terminalName, "terminalName cannot be null or empty");
    	code.nonce = com.google.common.base.Preconditions.checkNotNull(
    			nonce, "nonce cannot be empty");
        code.terminal = TerminalDetails.getInstance(
        		terminalAddress, HashUtils.sha256Key(terminalPublicKey));
    	return code;
    }

	public String getTerminalName() {
		return terminalName;
	}
	
	public Nonce getNonce() {
		return nonce;
	}

	@Override
	public byte[] getTerminalCommitment() {
		return terminal.getTerminalCommitment();
	}

	@Override
	public URI getTerminalAddress() {
		return terminal.getTerminalAddress();
	}

	@Override
	public boolean hasTerminal() {
		return (terminal != null && terminal.hasTerminal());
	}
}
