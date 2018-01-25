/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.service;

import java.io.IOException;

/**
 * Interface of an "accessor" which returns {@link Service} instances saved in a data store.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see Service
 * 
 */
public interface ServiceAccessor {

    /**
     * Query the store for a service with a given ID.
     * 
     * @param id ID to query for.
     * @return <code>Service</code> instance with matching ID or <code>null</code> if none could be
     *         found in the data store.
     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
     */
    public Service getServiceById(int id) throws IOException;

    /**
     * Query the store for a service with a given commitment.
     * 
     * @param commitment commitment to query for.
     * @return <code>Service</code> instance with matching commitment or <code>null</code> if none
     *         could be found in the data store.
     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
     */
    public Service getServiceByCommitment(byte[] commitment)
            throws IOException;
}
