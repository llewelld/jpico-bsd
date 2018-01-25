/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import uk.ac.cam.cl.pico.data.service.Service;

/**
 * Interface of a factory which produces concrete {@link KeyPairingImp} instances.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see KeyPairing
 * @see KeyPairingImp
 * 
 */
public interface KeyPairingImpFactory {
    public KeyPairingImp getImp(
            String name,
            Service service,
            PublicKey publicKey,
            PrivateKey privateKey);

    public KeyPairingImp getImp(String name, Service service, KeyPair keyPair);

    public KeyPairingImp getImp(KeyPairing keyPairing);
}
