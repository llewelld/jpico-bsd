/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

import java.util.Map;

import uk.ac.cam.cl.pico.data.service.Service;

/**
 * Interface of a factory which produces concrete {@link LensPairingImp} instances.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see LensPairing
 * @see LensPairingImp
 * 
 */
public interface LensPairingImpFactory {

    public LensPairingImp getImp(
            String name, Service service, Map<String, String> credentials);

    public LensPairingImp getImp(LensPairing credentialPairing);
}
