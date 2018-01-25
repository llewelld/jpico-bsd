/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.pairing;


import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Date;

import uk.ac.cam.cl.pico.data.Saveable;
import uk.ac.cam.cl.pico.data.service.Service;

/**
 * Pairing base class. All <code>Pairing</code> instances have an ID, an associated service, and a
 * human-readable name. <code>Pairing</code> subclasses include the credentials required for the
 * pairing to be used to authenticate to the associated service.
 * 
 * <p>
 * This class is part of a <a href="http://en.wikipedia.org/wiki/Bridge_pattern">Bridge pattern</a>.
 * Each <code>Pairing</code> instance has a reference to a concrete {@link PairingImp} instance. See
 * {@link uk.ac.cam.cl.pico.data.pairing} package documentation for more information on this
 * pattern. Specifically, a <code>Pairing</code> instance forwards the following methods to its
 * <code>PairingImp</code>:
 * <ul>
 * <li>{@link #save()}
 * <li>{@link #getId()}
 * <li>{@link #getService()}
 * <li>{@link #getName()}
 * <li>{@link #setName(String)}
 * </ul>
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see PairingImp
 * @see PairingImpFactory
 * @see PairingAccessor
 * 
 */
public class Pairing implements Saveable {

    private final PairingImp imp;

    /**
     * Construct a new <code>Pairing</code> instance using an existing <code>PairingImp</code>.
     * 
     * @param imp existing <code>PairingImp</code>.
     */
    public Pairing(final PairingImp imp) {
        this.imp = checkNotNull(imp);
    }

    /**
     * Construct a new <code>Pairing</code> instance.
     * 
     * @param factory factory to use to create the new <code>PairingImp</code>.
     * @param name human-readable name of the new pairing.
     * @param service service of the new pairing.
     * @throws NullPointerException if any argument is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is the empty string.
     */
    public Pairing(final PairingImpFactory factory,
            final String name, final Service service) {
        // Check arguments:
        checkNotNull(factory);
        Pairing.checkName(name);
        checkNotNull(service);

        imp = factory.getImp(name, service);
    }

    /**
     * Copy-constructor. Uses <code>factory</code> to create a new underlying
     * <code>PairingImp</code> from <code>pairing</code>.
     * 
     * @param factory factory to use to create the new <code>PairingImp</code>.
     * @param pairing instance to copy.
     */
    public Pairing(final PairingImpFactory factory, final Pairing pairing) {
        // Check arguments:
        checkNotNull(factory);
        checkNotNull(pairing);

        imp = factory.getImp(pairing);
    }

    /**
     * @return the <code>PairingImp</code> of this <code>Pairing</code>.
     */
    public PairingImp getImp() {
        return imp;
    }

    @Override
    public String toString() {
        return String.format(
                "<Pairing %d: \"%s\" for %s>",
                getId(),
                getName(),
                getService());
    }

    /**
     * Test for equality between <code>Pairing</code> instances.
     * 
     * @return <code>true</code> if the IDs of the <code>Pairing</code> instances are equal or
     *         <code>false</code> otherwise.
     * @throws IllegalStateException if both <code>Pairing</code> instances are unsaved (see
     *         {@link Saveable#isSaved()}).
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Pairing) {
            final Pairing other = (Pairing) obj;
            if (isSaved() || other.isSaved()) {
                return (getId() == other.getId());
            } else {
                throw new IllegalStateException(
                        "Cannot compare two unsaved Pairing instances.");
            }
        } else {
            return false;
        }
    }

    /**
     * @return the ID of this <code>Pairing</code> instance.
     */
    @Override
    public int hashCode() {
        return getId();
    }

    public void delete() throws IOException {
        imp.delete();
    }
    
    @Override
    public void save() throws IOException {
        imp.save();
    }

    @Override
    public boolean isSaved() {
        return imp.isSaved();
    }

    // Getters and setters

    /**
     * @return the ID of this pairing.
     */
    public int getId() {
        return imp.getId();
    }

    /**
     * @return the service this pairing is with.
     */
    public Service getService() {
        return imp.getService();
    }

    /**
     * @return the human-readable name of this pairing.
     */
    public String getName() {
        return imp.getName();
    }

    /**
     * Set the human-readable name of this pairing.
     * 
     * @param name new human-readable name.
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is the empty string.
     */
    public void setName(final String name) {
        imp.setName(Pairing.checkName(name));
    }

    public Date getDateCreated() {
        return imp.getDateCreated();
    }

    // Argument checks:

    /**
     * Check a potential pairing name. Pairing names cannot be <code>null</code> or the empty
     * string.
     * 
     * @param name potential pairing name.
     * @return the potential pairing name.
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is the empty string.
     */
    public static String checkName(final String name) {
        checkNotNull(name);
        checkArgument(!name.equals(""), "Pairing name cannot be empty string");
        return name;
    }
}