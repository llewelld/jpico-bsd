/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import uk.ac.cam.cl.pico.crypto.HashUtils;
import uk.ac.cam.cl.pico.data.Saveable;
import uk.ac.cam.cl.pico.data.service.Service;

/**
 * A pairing the Pico can use to authenticate to a non Pico-enabled website. Extends {@link Pairing}
 * to include traditional username/password credentials which can be used to authenticate to a non
 * Pico-enabled website.
 * 
 * <p>
 * This class is part of a <a href="http://en.wikipedia.org/wiki/Bridge_pattern">Bridge pattern</a>.
 * Each <code>LensPairing</code> instance has a reference to a concrete {@link LensPairingImp}
 * instance. See {@link uk.ac.cam.cl.pico.data.pairing} package documentation for more information
 * on this pattern. Specifically, a <code>LensPairing</code> instance forwards
 * {@link #getCredentials()} to its <code>LensPairingImp</code>.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see LensPairingImp
 * @see LensPairingImpFactory
 * @see LensPairingAccessor
 * 
 */
public class LensPairing extends Pairing implements Saveable {

    private LensPairingImp imp;

    /**
     * Construct a new <code>LensPairing</code> instance using an existing
     * <code>LensPairingImp</code>.
     * 
     * @param imp existing <code>LensPairingImp</code>.
     * @throws NullPointerException if <code>imp</code> is <code>null</code>.
     */
    public LensPairing(final LensPairingImp imp) {
        super(checkNotNull(imp));
        this.imp = imp;
    }

    /**
     * Construct a new <code>LensPairing</code> instance.
     * 
     * @param factory factory to use to create the new <code>LensPairingImp</code>.
     * @param name human-readable name of the new pairing.
     * @param service service of the new pairing.
     * @param credentials credentials of the new pairing.
     * @throws NullPointerException if any argument is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is the empty string or if
     *         <code>credentials</code> is empty.
     */
    public LensPairing(
            LensPairingImpFactory factory,
            String name,
            Service service,
            Map<String, String> credentials) {
        this(factory.getImp(
                Pairing.checkName(name),
                checkNotNull(service),
                LensPairing.validateCredentials(credentials)));
    }

    /**
     * Copy-constructor. Uses <code>factory</code> to create a new underlying
     * <code>LensPairingImp</code> from <code>credentialPairing</code>.
     * 
     * @param factory factory to use to create the new <code>LensPairingImp</code>.
     * @param credentialPairing instance to copy.
     */
    public LensPairing(
            LensPairingImpFactory factory,
            LensPairing credentialPairing) {
        this(factory.getImp(checkNotNull(credentialPairing)));
    }

    /**
     * @return a map containing the credentials of this lens pairing.
     */
    public Map<String, String> getCredentials() {
        return imp.getCredentials();
    }

    // Argument checks:

    /**
     * Check a potential credentials value. Must be a non-null, non-empty
     * <code>Map<String, String></code> instance.
     * 
     * @param credentials value to validate.
     * @return the potentials credentials value.
     * @throws NullPointerException if <code>credentials</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>credentials</code> has 0 entries.
     */
    public static Map<String, String> validateCredentials(
            Map<String, String> credentials)
            throws NullPointerException, IllegalArgumentException {
        checkNotNull(
                credentials,
                "CredentialPairing credentials cannot be null");
        checkArgument(
                credentials.size() > 0,
                "CredentialPairing credentials cannot be empty");
        return credentials;
    }

    /**
     * Create a pre-image resistant commitment of the web login form "action" (POST request address)
     * of the service.
     * 
     * @param serviceLoginAction web login form action of the service.
     * @return pre-image resistant commitment.
     */
    public static byte[] commitServiceLoginActon(URI serviceLoginAction) {
        return HashUtils.sha256(serviceLoginAction.toString());
    }
}
