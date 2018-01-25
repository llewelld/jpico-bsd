/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

import uk.ac.cam.cl.pico.data.service.Service;

/**
 * Interface of a factory which produces concrete {@link PairingImp} instances.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see Pairing
 * @see PairingImp
 * 
 */
public interface PairingImpFactory {

    public PairingImp getImp(String name, Service service);

    public PairingImp getImp(Pairing pairing);
}
