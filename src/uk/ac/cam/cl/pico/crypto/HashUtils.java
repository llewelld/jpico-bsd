/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import uk.ac.cam.cl.pico.comms.org.apache.commons.codec.binary.Base64;

/**
 * Some simple hashing utility functions.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
final public class HashUtils {

    private static final MessageDigest sha256;
    private static final Charset utf8;

    static {
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No SHA-256 algorithm available", e);
        }
        try {
            utf8 = Charset.forName("UTF-8");
        } catch (UnsupportedCharsetException e) {
            throw new RuntimeException("UTF-8 encoding not available", e);
        }
    }

    /**
     * UTF-8 encode then SHA-256 hash a {@link String}.
     * 
     * @param str input string.
     * @return SHA-256 hash of the UTF-8 encoding of <code>str</code>
     */
    public static byte[] sha256(final String str) {
        checkNotNull(str, "input string cannot be null");

        sha256.reset();
        sha256.update(str.getBytes(utf8));
        return sha256.digest();
    }

    /**
     * UTF-8 encode, SHA-256 hash, then base 64 encode a {@link String}.
     * 
     * @param str input string.
     * @return base 64 encoding of the SHA-256 hash of the UTF-8 encoding of <code>str</code>
     */
    public static String base64Sha256(final String str) {
        return Base64.encodeBase64String(sha256(str));
    }

    /**
     * Binary encode then SHA-256 hash a {@link Key} instance and return the raw byte array. To
     * binary encoded <code>key</code> this method calls the {@link Key#getEncoded() getEncoded}
     * method which returns the key encoded as a byte array using some standard format (typically
     * DER ASN.1).
     * 
     * @param key the Key instance to hash
     * @return SHA-256 hash of the binary encoding of <code>key</code>
     */
    public static byte[] sha256Key(final Key key) {
        checkNotNull(key);

        sha256.reset();
        sha256.update(key.getEncoded());
        return sha256.digest();
    }

    /**
     * Binary encode, SHA-256 hash, then base 64 encode a {@link Key} instance. To binary encoded
     * <code>key</code> method calls the {@link Key#getEncoded() getEncoded} method which returns
     * the key encoded as a byte array using some standard format (typically DER ASN.1).
     * 
     * @param key the Key instance to hash
     * @return base 64 encoding of the SHA-256 hash of the binary encoding of <code>key</code>
     */
    public static String base64Sha256Key(final Key key) {
        return Base64.encodeBase64String(sha256Key(key));
    }
}
