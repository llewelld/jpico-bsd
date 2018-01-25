/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Interface of concrete implementations underlying {@link KeyPairing} instances.
 * 
 * <p>
 * This interface is part of a <a href="http://en.wikipedia.org/wiki/Bridge_pattern">Bridge
 * pattern</a>. Each <code>KeyPairing</code> instance has a reference to a concrete
 * {@link KeyPairingImp} instance. See {@link uk.ac.cam.cl.pico.data.pairing} package documentation
 * for more information on this pattern.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see KeyPairing
 * @see KeyPairingImpFactory
 * 
 */
public interface KeyPairingImp extends PairingImp {

    PublicKey getPublicKey();

    PrivateKey getPrivateKey();
}
