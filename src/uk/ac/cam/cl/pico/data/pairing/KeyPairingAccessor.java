/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

import java.io.IOException;
import java.util.List;

/**
 * Interface of an "accessor" which returns {@link KeyPairing} instances saved in a data store.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see KeyPairing
 * 
 */
public interface KeyPairingAccessor {

    /**
     * Query the store for a key pairing with a given ID.
     * 
     * @param pairingId ID to query for.
     * @return <code>KeyPairing</code> instance with matching ID or <code>null</code> if none could
     *         be found.
     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
     */
    public KeyPairing getKeyPairingById(int pairingId) throws IOException;

    /**
     * Query the store for key pairings with a service with a given public key.
     * 
     * @param servicePublicKey service public key to query for.
     * @return <code>KeyPairing</code> instances with service with matching public key.
     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
     */
    public List<KeyPairing> getKeyPairingsByServiceCommitment(
            byte[] commitment) throws IOException;
    
    /**
     * Get a list of all key pairings in the store.
     * 
     * @return all <code>KeyPairing</code> instances.
     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
     */
    public List<KeyPairing> getAllKeyPairings() throws IOException;
}
