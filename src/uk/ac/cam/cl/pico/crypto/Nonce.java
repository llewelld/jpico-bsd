/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;

import java.security.SecureRandom;
import java.util.Arrays;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

import uk.ac.cam.cl.pico.comms.org.apache.commons.codec.binary.Base64;

/**
 * In cryptography a nonce is an arbitrary number used only once in a cryptographic communication. 
 * A Nonce instance encapsulates the value as a byte array. The Nonce class provides two static
 * factory methods for constructing either a random instance, or an instance with a given value.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 */
final public class Nonce implements Destroyable {

    final private static int DEFAULT_BYTES = 8;
    final private byte[] value;  
    private boolean isDestroyed = false;

    private Nonce(final byte[] value) {
        // Verify the method's preconditions
        assert (value != null);
        this.value = value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Nonce) {
            final Nonce other = (Nonce) obj;
            // Destroyed nonces can't be compared
            if (isDestroyed() || other.isDestroyed()) {
                throw new IllegalStateException("Nonce is destroyed");
            }
            return Arrays.equals(value, other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
    
    @Override
    public String toString() {
        return Base64.encodeBase64String(value);     
    }
    
    /**
     * Accessor method for the Nonce instance's value.
     * 
     * @return The encapsulated byte[] value of the Nonce instance.
     */
    public byte[] getValue() {
        if (isDestroyed) {
            // None is already destroyed
            throw new IllegalStateException("Nonce is destroyed");
        }

        // Return defensive copy of the Nonce instance's value 
        return Arrays.copyOf(value, value.length);
    }

    /**
     * Factory constructor method for creating a new Nonce instance with a random value.
     * 
     * @return Nonce new random instance.
     */
    public static Nonce getRandomInstance() {
        // Create the random value encapsulated by the Nonce instance
        final SecureRandom random = new SecureRandom();
        final byte[] value = new byte[DEFAULT_BYTES];
        random.nextBytes(value);

        return new Nonce(value);
    }

    /**
     * Factory constructor method for creating a new Nonce instance with a given value.
     * 
     * @param value The value to instantiate the Nonce with.
     * @return Nonce instance with the given value.
     */
    public static Nonce getInstance(final byte[] value) {
        // Verify the method's preconditions
        checkNotNull(value);

        return new Nonce(value);
    }

    // implements Destroyable

    @Override
    public void destroy() throws DestroyFailedException {
        if (isDestroyed) {
            // None is already destroyed
            throw new IllegalStateException("Nonce is already destroyed");
        }

        Arrays.fill(value, (byte) 0);
        isDestroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }
}