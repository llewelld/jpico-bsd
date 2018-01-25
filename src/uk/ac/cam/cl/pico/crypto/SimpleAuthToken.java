/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * A simple AuthToken implementation which just contains a String.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 */
final public class SimpleAuthToken implements AuthToken {

    /**
     * Charset used when turning Strings into byte arrays. Constant: {@value #STRING_CHARSET}
     */
    public static final Charset STRING_CHARSET = Charset.forName("UTF-8");

    /**
     * Token type byte for this AuthToken subclass. Constant: {@value #TOKEN_TYPE_BYTE}
     */
    public static final byte TOKEN_TYPE_BYTE = 0x02;

    private final String authString;

    public SimpleAuthToken(final String authString) {
        // Verify the method's preconditions
        if (authString == null)
            throw new NullPointerException();

        this.authString = authString;
    }

    /**
     * Return the simple authentication String.
     */
    @Override
    public String getFull() {
        return authString;
    }

    /**
     * Return the simple authentication String.
     */
    @Override
    public String getFallback() {
        return authString;
    }

    /**
     * SimpleAuthToken instance ares equal to each other if the String value is the same.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof SimpleAuthToken) {
            final SimpleAuthToken other = (SimpleAuthToken) obj;
            return (authString.equals(other.authString));
        } else {
            return false;
        }
    }

    /**
     * Return this SimpleAuthToken token encoded as a byte array.
     */
    @Override
    public byte[] toByteArray() {

        byte[] stringBytes = authString.getBytes(STRING_CHARSET);
        byte[] result = new byte[stringBytes.length + 1];

        // Identify the type of AuthToken as a SimpleAuthToken
        result[0] = SimpleAuthToken.TOKEN_TYPE_BYTE;
        System.arraycopy(stringBytes, 0, result, 1, stringBytes.length);

        return result;
    }

    /**
     * Reconstruct a BrowserAuthToken instance from a byte array,
     * 
     * @param bytes byte array containing an encoded SimpleAuthToken.
     * @return reconstructed SimpleAuthToken instance.
     * @throws IOException if the token type byte is invalid or the token has been incorrectly
     *         serialised.
     */
    public static SimpleAuthToken fromByteArray(byte[] bytes) throws IOException {
        // Verify the AuthToken type is correct
        final byte tokenTypeByte = bytes[0];
        if (tokenTypeByte != TOKEN_TYPE_BYTE) {
            throw new IOException(
                    "Read token type byte (" + tokenTypeByte +
                            ") did not match SimpleAuthToken.TOKEN_TYPE_BYTE (" +
                            TOKEN_TYPE_BYTE + ")");
        }
        final String authString = new String(
                Arrays.copyOfRange(bytes, 1, bytes.length),
                SimpleAuthToken.STRING_CHARSET);
        return new SimpleAuthToken(authString);
    }
}
