/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

import com.google.common.base.Objects;

import java.security.PublicKey;
import java.util.Arrays;

import uk.ac.cam.cl.pico.crypto.Nonce;

/**
 * The first message of the SIGMA-I protocol, which is sent from the Pico to the service.
 * <p>
 * A <code>StartMessage</code> contains two items:
 * <li><code>picoEphemeralPublicKey</code> - A Diffie-Hellman exponential which will be combined
 * with another one from the service to form the symmetric keys for this session.
 * <li><code>picoNonce</code> - A nonce to ensure freshness of the service's response.</li> </ul>
 * <p>
 * This message does not have an encrypted form, all fields are sent in the clear.
 * <p>
 * The next message in the protocol is the {@link ServiceAuthMessage}.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
public final class StartMessage extends Message {

    private final byte picoVersion;
    private final PublicKey picoEphemeralPublicKey;
    private final Nonce picoNonce;

    StartMessage(byte picoVersion, PublicKey picoEphemeralPublicKey, Nonce picoNonce) {
        this.picoVersion = picoVersion;
        this.picoEphemeralPublicKey = picoEphemeralPublicKey;
        this.picoNonce = picoNonce;
    }

    /**
     * The Pico's "Pico Protocol" version.
     * 
     * @return a byte representing the protocol version number
     * 
     */
    public byte getPicoVersion() {
        return picoVersion;
    }

    public PublicKey getPicoEphemeralPublicKey() {
        return picoEphemeralPublicKey;
    }

    public Nonce getPicoNonce() {
        return picoNonce;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StartMessage) {
            StartMessage other = (StartMessage) obj;
            return Arrays.equals(this.picoEphemeralPublicKey.getEncoded(),
                    other.picoEphemeralPublicKey.getEncoded())
                    && this.picoNonce.equals(other.picoNonce);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(picoVersion, picoEphemeralPublicKey, picoNonce);
    }
    
    public static final StartMessage getInstance(
            byte picoVersion, PublicKey picoEphemeralPublicKey, Nonce picoNonce) {
        return new StartMessage(picoVersion, picoEphemeralPublicKey, picoNonce);
    }
}
