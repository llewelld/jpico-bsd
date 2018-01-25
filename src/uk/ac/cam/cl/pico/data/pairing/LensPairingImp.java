/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

import java.util.Map;

/**
 * Interface of concrete implementations underlying {@link LensPairing} instances.
 * 
 * <p>
 * This interface is part of a <a href="http://en.wikipedia.org/wiki/Bridge_pattern">Bridge
 * pattern</a>. Each <code>LensPairing</code> instance has a reference to a concrete
 * {@link LensPairingImp} instance. See {@link uk.ac.cam.cl.pico.data.pairing} package documentation
 * for more information on this pattern.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see LensPairing
 * @see LensPairingImpFactory
 * 
 */
public interface LensPairingImp extends PairingImp {

    Map<String, String> getCredentials();
}
