/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import java.io.IOException;

/**
 * Factory for reconstructing serialised AuthToken instances.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 */
final public class AuthTokenFactory {

    /**
     * Reconstruct and AuthToken from a byte array.
     * 
     * @param bytes AuthToken as a byte array.
     * @return AuthToken instance.
     * @throws IOException if the token type byte is not recognised of the token has been
     *         incorrectly serialised.
     */
    public static AuthToken fromByteArray(final byte[] bytes)
            throws IOException {

        final byte tokenTypeByte = bytes[0];

        if (tokenTypeByte == BrowserAuthToken.TOKEN_TYPE_BYTE) {
            return BrowserAuthToken.fromByteArray(bytes);
        }
        else if (tokenTypeByte == SimpleAuthToken.TOKEN_TYPE_BYTE) {
            return SimpleAuthToken.fromByteArray(bytes);
        }
        else {
            throw new IOException(
                    "Unrecognised token type byte (" + tokenTypeByte + ")");
        }
    }
}
