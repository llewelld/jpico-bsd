/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data;

import java.io.IOException;

/**
 * An object which can be saved to a permanent data store such as a database.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
public interface Saveable {

    /**
     * Save this object to a permanent data store. The object is responsible for choosing where to
     * save itself to.
     * 
     * @throws IOException if an <code>IOException</code> occurs whilst saving.
     */
    public void save() throws IOException;

    /**
     * Check whether or not this saveable object has been saved yet.
     * 
     * @return <code>true</code> if it has been saved or <code>false</code> otherwise.
     */
    boolean isSaved();
}
