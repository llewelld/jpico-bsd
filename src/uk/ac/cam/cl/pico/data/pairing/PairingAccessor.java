/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

import java.io.IOException;

/**
 * Interface of an "accessor" which returns {@link Pairing} instances saved in a data store.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see Pairing
 * 
 */
public interface PairingAccessor {

    /**
     * Query the store for a pairing with a given ID.
     * 
     * @param id ID to query for.
     * @return <code>Pairing</code> instance with matching ID or <code>null</code> if none could be
     *         found.
     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
     */
    public Pairing getPairingById(int id) throws IOException;
}
