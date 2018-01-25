/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.service;

import java.net.URI;

import uk.ac.cam.cl.pico.data.Saveable;

/**
 * Interface of concrete implementations underlying {@link Service} instances.
 * 
 * <p>
 * This interface is part of a <a href="http://en.wikipedia.org/wiki/Bridge_pattern">Bridge
 * pattern</a>. Each <code>Service</code> instance has a reference to a concrete {@link ServiceImp}
 * instance. See {@link uk.ac.cam.cl.pico.data.service} package documentation for more information
 * on this pattern.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see Service
 * @see ServiceImpFactory
 * 
 */
public interface ServiceImp extends Saveable {

    int getId();

    String getName();

    URI getAddress();

    byte[] getCommitment();

    void setAddress(URI checkNotNull);
}
