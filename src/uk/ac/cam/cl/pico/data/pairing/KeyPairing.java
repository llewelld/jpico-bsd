/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import uk.ac.cam.cl.pico.crypto.HashUtils;
import uk.ac.cam.cl.pico.data.service.Service;

/**
 * A pairing the Pico can use to authenticate to a Pico-enabled service. Extends {@link Pairing} to
 * include an asymmetric key pair which the Pico can use to carry out the key-based
 * <em>Pico authentication protocol</em>.
 * 
 * <p>
 * This class is part of a <a href="http://en.wikipedia.org/wiki/Bridge_pattern">Bridge pattern</a>.
 * Each <code>KeyPairing</code> instance has a reference to a concrete {@link KeyPairingImp}
 * instance. See {@link uk.ac.cam.cl.pico.data.pairing} package documentation for more information
 * on this pattern. Specifically, a <code>KeyPairing</code> instance forwards
 * {@link #getPublicKey()} and {@link #getPrivateKey()} to its <code>KeyPairingImp</code>.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see KeyPairingImp
 * @see KeyPairingImpFactory
 * @see KeyPairingAccessor
 * 
 */
public class KeyPairing extends Pairing {

    private final KeyPairingImp imp;

    /**
     * Construct a new <code>KeyPairing</code> instance using an existing <code>KeyPairingImp</code>
     * .
     * 
     * @param imp existing <code>KeyPairingImp</code>.
     * @throws NullPointerException if <code>imp</code> is <code>null</code>.
     */
    public KeyPairing(final KeyPairingImp imp) {
        super(checkNotNull(imp));
        this.imp = imp;
    }

    /**
     * Construct a new <code>KeyPairing</code> instance.
     * 
     * @param factory factory to use to create the new <code>LensPairingImp</code>.
     * @param name human-readable name of the new pairing.
     * @param service service of the new pairing.
     * @param publicKey public key of the new pairing.
     * @param privateKey private key of the new pairing.
     * @throws NullPointerException if any argument is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is the empty string.
     */
    public KeyPairing(
            KeyPairingImpFactory factory,
            String name,
            Service service,
            PublicKey publicKey,
            PrivateKey privateKey) {
        this(factory.getImp(
                Pairing.checkName(name),
                checkNotNull(service, "KeyPairing service cannot be null"),
                checkNotNull(publicKey, "KeyPairing public key cannot be null"),
                checkNotNull(privateKey, "KeyPairing private key cannot be null")));
    }

    /**
     * Construct a new <code>KeyPairing</code> instance.
     * 
     * @param factory factory to use to create the new <code>LensPairingImp</code>.
     * @param name human-readable name of the new pairing.
     * @param service service of the new pairing.
     * @param keyPair public/private key pair of the new pairing.
     * @throws NullPointerException if any argument is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is the empty string.
     */
    public KeyPairing(
            KeyPairingImpFactory factory,
            String name,
            Service service,
            KeyPair keyPair) {
        this(factory.getImp(
                Pairing.checkName(name),
                checkNotNull(service, "KeyPairing service cannot be null"),
                checkNotNull(keyPair, "KeyPairing cannot be constructed with a null key pair")));
    }

    /**
     * Copy-constructor. Uses <code>factory</code> to create a new underlying
     * <code>KeyPairingImp</code> from <code>keyPairing</code>.
     * 
     * @param factory factory to use to create the new <code>KeyPairingImp</code>.
     * @param keyPairing instance to copy.
     */
    public KeyPairing(KeyPairingImpFactory factory, KeyPairing keyPairing) {
        this(factory.getImp(checkNotNull(keyPairing)));
    }

    @Override
    public String toString() {
        return String.format(
                "<KeyPairing %d: \"%s\" for %s>",
                getId(),
                getName(),
                getService());
    }

    /**
     * @return the public key of this key pairing.
     */
    public PublicKey getPublicKey() {
        return imp.getPublicKey();
    }

    /**
     * @return the private get of this key pairing.
     */
    public PrivateKey getPrivateKey() {
        return imp.getPrivateKey();
    }

    /**
     * Create a pre-image resistant commitment of the long-term identity public key of a service.
     * 
     * @param servicePublicKey public key of the service.
     * @return pre-image resistant commitment.
     */
    public static byte[] commitServicePublicKey(PublicKey servicePublicKey) {
        return HashUtils.sha256Key(servicePublicKey);
    }
}
