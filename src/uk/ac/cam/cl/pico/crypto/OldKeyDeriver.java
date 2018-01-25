/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.Destroyable;

import uk.ac.cam.cl.pico.config.Config;
import uk.ac.cam.cl.pico.crypto.SigmaKeyDeriver.DerivedKeys;

/**
 * A KeyDeriver is used to derive one or more cryptographic keys from a shared secret established by
 * a a public-key-based key establishment scheme, such as Diffie-Hellman key exchange.
 * 
 * <p>
 * This class was implemented according to the following NIST recommendations:
 * 
 * <ol>
 * <li>NIST SP 800-56C, Recommendation for Key Derivation through Extraction-then-Expansion (<a
 * href= "csrc.nist.gov/publications/nistpubs/800-56C/SP-800-56C.pdf">csrc.nist.gov/publications/
 * nistpubs /800-56C/SP-800-56C.pdf</a>‎)</li>
 * <li>
 * </ol>
 * 
 * <p>
 * Specifically the class uses the general scheme set out in publication 800-56C, in which there are
 * two steps: a randomness extraction step, followed by a key expansion step. The result of the
 * randomness extraction step is K<sub>DK</sub>, the key derivation key, which is then the key for
 * the hash function used during the key expansion step.
 * 
 * @author Chris Warrington <cw471@cl.cam.ac.uk>
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
@Deprecated
final class OldKeyDeriver implements Destroyable {

    final private Mac hmac;
    final private byte[] nonces;

    /**
     * This holds the shared secret ready to be destroyed .
     * 
     * @author cw471
     * 
     */
    final private class SecretContainer implements Destroyable {

        private final byte[] sharedSecret;
        private boolean isDestroyed = false;

        public SecretContainer(byte[] sharedSecret) {

            if (sharedSecret == null)
                throw new NullPointerException();

            this.sharedSecret = sharedSecret;
        }

        @Override
        public void destroy() {

            if (isDestroyed)
                throw new IllegalStateException();

            OldKeyDeriver.destroySecret(this.sharedSecret);
            isDestroyed = true;
        }

        @Override
        public boolean isDestroyed() {
            return isDestroyed;
        }

    }

    private boolean isDestroyed = false;
    final private SecretContainer sharedSecretContainer;
    private byte currentBlockNumber = 0;
    private byte[] currentBlock;

    /**
     * The security provider.
     */
    private static String securityProvider;

    // Initialise the security provider
    static {

        Config config = Config.getInstance();

        OldKeyDeriver.securityProvider = (String) config.get("crypto.provider");
    }

    /**
     * Derives all of the keys from the ecdhSharedSecret for the SigmaProver and the SigmaVerifier
     * 
     * Warning: This the shared secret byte array is destroyed with the class
     * 
     * @param sharedSecret The ECDH generated shared secret
     * @param picoNonce The nonce supplied by the Pico.
     * @param serviceNonce The nonce supplied by the service.
     * @throws CryptoRuntimeException An error occured using the crypto libraries.
     */
    @Deprecated
    public OldKeyDeriver(
            final byte[] sharedSecret,
            final Nonce picoNonce,
            final Nonce serviceNonce) throws CryptoRuntimeException {

        sharedSecretContainer = new SecretContainer(sharedSecret);

        // Make 'key' for HMAC from pico nonce and service nonce.
        nonces = concatByteArrays(picoNonce.getValue(),
                serviceNonce.getValue());
        final SecretKey noncesKeyForHmac =
                new SecretKeySpec(nonces, "Hmac-SHA256");

        // Generate Key derivation key

        try {
            hmac = Mac.getInstance("Hmac-SHA256", OldKeyDeriver.securityProvider);
            hmac.init(noncesKeyForHmac);
            final byte[] keyDerivationKey = hmac.doFinal(sharedSecret);

            // Where do SPIi and SPIr come from? They're omitted here!

            // All of the derived keys use the same key into the KMAC
            // See RFC4306 2.14: Keying Material for IKE_SA
            // sp8000-135 5.1

            hmac.init(new SecretKeySpec(keyDerivationKey, "Hmac-SHA256"));

        } catch (NoSuchAlgorithmException e) {
            throw new CryptoRuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new CryptoRuntimeException(e);
        } catch (InvalidKeyException e) {
            // TODO: Perhaps should be handled differently,
            // as could be caused by the remote.
            throw new CryptoRuntimeException(e);
        }

    }

    private static void destroySecret(final byte[] secret) {
        Arrays.fill(secret, (byte) 0);
    }

    private static void destroySecret(byte secret) {
        secret = 0;
    }

    private SecretKey getNewKey(final String algorithm) {
        generateNextBlock();
        return new SecretKeySpec(currentBlock, algorithm);
    }

    public DerivedKeys deriveKeys() {
        // Note each call to getNewKey advances a block of key material,
        // so order is important.
        return new DerivedKeys(getNewKey("SHA256WITHECDSA"),
                getNewKey("AES/GCM/NoPadding"), getNewKey("SHA256WITHECDSA"),
                getNewKey("AES/GCM/NoPadding"), getNewKey("AES/GCM/NoPadding"));
    }

    //
    private void generateNextBlock() {
        if (isDestroyed)
            throw new IllegalStateException();

        this.currentBlockNumber += 1;

        if (this.currentBlock == null) {
            // First block (block 1)
            this.currentBlock =
                    hmac.doFinal(concatByteArrays(nonces,
                            new byte[] {currentBlockNumber}));
        } else {
            // Subsequent blocks
            this.currentBlock =
                    hmac.doFinal(concatByteArrays(
                            currentBlock,
                            this.nonces,
                            new byte[] {currentBlockNumber}));
        }

    }

    private static byte[] concatByteArrays(final byte[]... arrays) {
        int totalLength = 0;
        for (byte[] b : arrays) {
            totalLength += b.length;
        }
        byte[] newArray = new byte[totalLength];
        int currentEnd = 0;
        for (byte[] b : arrays) {
            System.arraycopy(b, 0, newArray, currentEnd, b.length);
            currentEnd += b.length;
        }

        return newArray;
    }

    @Override
    public void destroy() {
        isDestroyed = true;

        // Probably over the top...
        hmac.reset();
        destroySecret(nonces);
        destroySecret(currentBlock);
        destroySecret(currentBlockNumber);
        sharedSecretContainer.destroy();

    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

}
