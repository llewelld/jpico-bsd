/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.terminal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import uk.ac.cam.cl.pico.Preconditions;
import uk.ac.cam.cl.pico.data.Saveable;

public class Terminal implements Saveable {
	
	public interface Imp extends Saveable {
		public int getId();
		public String getName();
		public byte[] getCommitment();
		public PublicKey getPicoPublicKey();
		public PrivateKey getPicoPrivateKey();
		public void delete() throws IOException;
	}
	
	public interface ImpFactory {
		Imp getImp(
				String name,
				byte[] commitment,
				PublicKey picoPublicKey,
				PrivateKey picoPrivateKey);
		Imp getImp(Terminal terminal);
	}
	
	public interface Accessor {
		/**
	     * Query the store for a terminal with a given ID.
	     * 
	     * @param id ID to query for.
	     * @return <code>Terminal</code> instance with matching ID or <code>null</code> if none could be
	     *         found in the data store.
	     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
	     */
	    public Terminal getTerminalById(int id) throws IOException;

	    /**
	     * Query the store for a terminal with a given commitment.
	     * 
	     * @param commitment commitment to query for.
	     * @return <code>Terminal</code> instance with matching commitment or <code>null</code> if none
	     *         could be found in the data store.
	     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
	     */
	    public Terminal getTerminalByCommitment(byte[] commitment)
	            throws IOException;
	    
	    /**
	     * Query the store for all terminals.
	     * 
	     * @return a list containing all terminals in the data store.
	     * @throws IOException is an <code>IOException</code> occurred whilst querying the data
	     * 	store.
	     */
	    public List<Terminal> getAllTerminals() throws IOException;
	}
	
	private final Imp imp;
	
	/**
	 * Construct a <code>Terminal</code> instance using an existing
	 * <code>TerminalImp</code>.
	 * 
	 * @param imp existing <code>TerminalImp</code>.
	 * 
	 * @throws NullPointerException if <code>imp</code> is <code>null</code>.
	 */
	public Terminal(final Imp imp) {
		this.imp = checkNotNull(imp, "imp cannot be null");
	}
	
	public Terminal(
			final ImpFactory factory,
			final String name,
			final byte[] commitment,
			final PublicKey picoPublicKey,
			final PrivateKey picoPrivateKey) {
		this(factory.getImp(
				Preconditions.checkNotNullOrEmpty(name, "name cannot be null or empty"),
				Preconditions.checkNotNullOrEmpty(commitment, "commitment cannot be null or empty"),
				checkNotNull(picoPublicKey, "picoPublicKey cannot be null"),
				checkNotNull(picoPrivateKey, "picoPrivateKey cannot be null")));
	}
	
	public Terminal(
			final ImpFactory factory,
			final String name,
			final byte[] commitment,
			final KeyPair picoKeyPair) {
		this(
				factory, 
				name, 
				commitment, 
				checkNotNull(picoKeyPair, "picoKeyPair cannot be null").getPublic(),
				picoKeyPair.getPrivate());
	}
	
	@Override
    public String toString() {
        return String.format("<Terminal %d: \"%s\">", getId(), getName());
    }

    /**
     * Test for equality between <code>Service</code> instances.
     * 
     * @return <code>true</code> if the IDs of the <code>Terminal</code> instances are equal or
     *         <code>false</code> otherwise.
     * @throws IllegalStateException if both <code>Terminal</code> instances are unsaved (see
     *         {@link Saveable#isSaved()}).
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Terminal) {
            final Terminal other = (Terminal) obj;
            if (isSaved() || other.isSaved()) {
                return (getId() == other.getId());
            } else {
                throw new IllegalStateException(
                        "Cannot compare two unsaved Terminal instances.");
            }
        } else {
            return false;
        }
    }

    /**
     * @return the ID of this <code>Terminal</code> instance.
     */
    @Override
    public int hashCode() {
        return getId();
    }

	@Override
	public void save() throws IOException {
		imp.save();
	}

	@Override
	public boolean isSaved() {
		return imp.isSaved();
	}
	
	public void delete() throws IOException {
		imp.delete();
	}
	
	/**
	 * @return the ID of this terminal.
	 */
	public int getId() {
		return imp.getId();
	}
	
	/**
	 * @return the human-readable name for this terminal.
	 */
	public String getName() {
		return imp.getName();
	}
	
	/**
	 * @return the commitment of this terminal.
	 */
	public byte[] getCommitment() {
		return imp.getCommitment();
	}
	
	/**
	 * @return the public key the Pico uses when authenticating to this terminal.
	 */
	public PublicKey getPicoPublicKey() {
		return imp.getPicoPublicKey();
	}
	
	/**
	 * @return the private key the Pico uses when authenticating to this terminal.
	 */
	public PrivateKey getPicoPrivateKey() {
		return imp.getPicoPrivateKey();
	}
}
