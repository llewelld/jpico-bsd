/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

import com.google.gson.annotations.SerializedName;

import java.net.URI;

import uk.ac.cam.cl.pico.Preconditions;

final class TerminalDetails implements WithTerminalDetails {
	
	static TerminalDetails getInstance(URI address, byte[] commitment) {
		final TerminalDetails details = new TerminalDetails();
		details.address = Preconditions.checkNotNullOrEmpty(
        		address, "address cannot be null or empty");
        details.commitment = Preconditions.checkNotNullOrEmpty(
        		commitment, "commitment cannot be null or empty");
        return details;
	}
	
	static TerminalDetails getEmptyInstance() {
		return new TerminalDetails();
	}
	
	@SerializedName("ta") private URI address;
	@SerializedName("tc") private byte[] commitment;
	
	// no-args constructor for Gson
	private TerminalDetails() {};

	@Override
	public URI getTerminalAddress() {
		return address;
	}

	@Override
	public byte[] getTerminalCommitment() {
		return commitment;
	}

	@Override
	public boolean hasTerminal() {
		// Check that address and commitment are both non-null and non-empty.
		return (
				address != null &&
				address.toString().length() > 0 &&
				commitment != null &&
				commitment.length > 0);
	}
}
