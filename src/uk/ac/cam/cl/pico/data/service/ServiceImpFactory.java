/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.service;

import java.net.URI;



/**
 * Interface of a factory which produces concrete {@link ServiceImp} instances.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see Service
 * @see ServiceImp
 * 
 */
public interface ServiceImpFactory {

    public ServiceImp getImp(String name, URI address, byte[] commitment);

    public ServiceImp getImp(Service service);
}
