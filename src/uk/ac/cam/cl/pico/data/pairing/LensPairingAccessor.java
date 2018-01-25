/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface of an "accessor" which returns {@link LensPairing} instances saved in a data store.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see LensPairing
 * 
 */
public interface LensPairingAccessor {

    /**
     * Query the store for a lens pairing with a given ID.
     * 
     * @param pairingId ID to query for.
     * @return <code>LensPairing</code> instance with matching ID or <code>null</code> if none could
     *         be found.
     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
     */
    public LensPairing getLensPairingById(int pairingId)
            throws IOException;

    /**
     * Query the store for lens pairings with a website service with a given login form action.
     * 
     * @param serviceLoginAction service login form action to query for.
     * @return <code>LensPairing</code> instances with service with matching login form action.
     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
     */
    public List<LensPairing> getLensPairingsByServiceCommitment(
            byte[] serviceLoginAction) throws IOException;
    
    /**
     * Query the store for lens pairings with a website service with a given set of login form credentials.
     * 
     * @param serviceLoginAction service login form action to query for.
     * @param serviceLoginAction service login form action to query for.
     * @param credentials service login form action to query for.
     * @return <code>LensPairing</code> instances with service with matching login form action and
     * credentials.
     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
     */
    public List<LensPairing> getLensPairingsByServiceCommitmentAndCredentials(
            byte[] serviceLoginAction, final Map<String, String> credentials) throws IOException;
    
    /**
     * Get a list of all lens pairings in the store.
     * 
     * @return all <code>LensPairing</code> instances.
     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
     */
    public List<LensPairing> getAllLensPairings() throws IOException;
}
