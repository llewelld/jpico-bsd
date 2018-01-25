/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

import java.net.URI;

/**
 * Interface of a {@link VisualCode} which may include the details of a terminal for session
 * delegation.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk> 
 *
 */
public interface WithTerminalDetails {
	
	/**
	 * @return the address of the terminal, or <code>null</code> if terminal details 
	 *  are not present.
	 */
	URI getTerminalAddress();
	
	/**
	 * Return the terminal's commitment, which is the hash of its public key (see
	 * {@link Terminal#getCommitment()}).
	 * 
	 * @return the commitment of the terminal, or <code>null</code> if terminal details 
	 *  are not present.
	 */
	byte[] getTerminalCommitment();
	
	/**
	 * Check whether this visual code includes the terminal details or not. Session delegation is
	 * not necessary for some applications and in these cases no terminal details are included in
	 * any visual code.
	 * 
	 * @return <code>true</code> if the terminal address and commitment are present and 
	 * 	<code>false</code> otherwise.
	 */
	boolean hasTerminal();
}
