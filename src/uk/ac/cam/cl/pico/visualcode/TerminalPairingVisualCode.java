/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

import com.google.gson.annotations.SerializedName;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.security.PublicKey;

import uk.ac.cam.cl.pico.Preconditions;
import uk.ac.cam.cl.pico.crypto.HashUtils;
import uk.ac.cam.cl.pico.crypto.Nonce;

public final class TerminalPairingVisualCode extends VisualCode 
		implements WithTerminalDetails {
	
    public static String TYPE = "TP";
    
	@SerializedName("tn") private String terminalName;
	@SerializedName("n") private Nonce nonce;
	@SerializedName("td") private TerminalDetails terminal;

	private TerminalPairingVisualCode() {
		super(TYPE);
	}
	
	public static TerminalPairingVisualCode getInstance(
			String terminalName,
			Nonce nonce,
			URI terminalAddress,
			PublicKey terminalPublicKey) {
		TerminalPairingVisualCode code = new TerminalPairingVisualCode();
		code.terminalName = Preconditions.checkNotNullOrEmpty(
				terminalName, "terminalName cannot be null or empty");
		code.nonce = checkNotNull(nonce, "nonce cannot be null");
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
