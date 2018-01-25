/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

import java.io.IOException;
import java.util.Date;

import uk.ac.cam.cl.pico.data.Saveable;
import uk.ac.cam.cl.pico.data.service.Service;

/**
 * Interface of concrete implementations underlying {@link Pairing} instances.
 * 
 * <p>
 * This interface is part of a <a href="http://en.wikipedia.org/wiki/Bridge_pattern">Bridge
 * pattern</a>. Each <code>Pairing</code> instance has a reference to a concrete {@link PairingImp}
 * instance. See {@link uk.ac.cam.cl.pico.data.pairing} package documentation for more information
 * on this pattern.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see Pairing
 * @see PairingImpFactory
 * 
 */
public interface PairingImp extends Saveable {

    int getId();

    Service getService();

    String getName();

    void setName(String name);

    Date getDateCreated();
    
    public void delete() throws IOException;
}
