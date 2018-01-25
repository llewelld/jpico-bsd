/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.session;

import java.io.IOException;

/**
 * Interface of an "accessor" which returns {@link Session} instances saved in a data store.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 * @see Session
 * 
 */
public interface SessionAccessor {

    /**
     * Query the store for a session with a given ID.
     * 
     * @param sessionId  ID to query for.
     * @return <code>Session</code> instance with matching ID or <code>null</code> if none could
     *         be found.
     * @throws IOException if an <code>IOException</code> occurred whilst querying the data store.
     */
    public Session getSessionById(int sessionId) throws IOException;
}
