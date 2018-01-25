/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

import com.google.gson.annotations.SerializedName;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import uk.ac.cam.cl.pico.Preconditions;
import uk.ac.cam.cl.pico.crypto.CryptoFactory;
import uk.ac.cam.cl.pico.data.pairing.KeyPairing;

/**
 * VisualCode containing details to pair with a service.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 */
final public class KeyPairingVisualCode extends KeyVisualCode implements SignedVisualCode {
	
    public static String TYPE = "KP";
    
    public static KeyPairingVisualCode getSignedInstance(
            final URI serviceAddress,
            final URI terminalAddress,
            final byte[] terminalCommitment,
            final String serviceName,
            final KeyPair serviceKeyPair) throws InvalidKeyException {
    	return getSignedInstance(serviceAddress, terminalAddress, terminalCommitment, serviceName, serviceKeyPair, null);
    }
    
    /**
     * Returns a new signed <code>KeyPairingVisualCode</code> instance with terminal fields set.
     * 
	 * @throws InvalidKeyException if an ECDSA signature cannot be created with the supplied key
	 * 	pair.
     */
    public static KeyPairingVisualCode getSignedInstance(
            final URI serviceAddress,
            final URI terminalAddress,
            final byte[] terminalCommitment,
            final String serviceName,
            final KeyPair serviceKeyPair,
            final byte[] extraData) throws InvalidKeyException {
    	final KeyPairingVisualCode code = new KeyPairingVisualCode();
    	code.serviceAddress = Preconditions.checkNotNullOrEmpty(
    			serviceAddress, "serviceAddress cannot be null or empty");
    	code.serviceName = Preconditions.checkNotNullOrEmpty(
    			serviceName, "serviceName cannot be null or empty");
    	code.extraData = extraData;
    	// Add terminal details
    	code.terminal = TerminalDetails.getInstance(terminalAddress, terminalCommitment);
    	
    	// Add key and sign
    	checkNotNull(serviceKeyPair, "serviceKeyPair cannot be null");
    	code.servicePublicKey = serviceKeyPair.getPublic();
    	try {
			code.sign(serviceKeyPair.getPrivate());
		} catch (SignatureException e) {
			// Re-thrown because this exception indicates that the signature cannot be created
			// using the bytes returned by getBytesToSign, which should only occur as a result of
			// a program error.
			throw new RuntimeException("unable to create signature", e);
		}
        return code;
    }
    
    public static KeyPairingVisualCode getSignedInstanceNoTerminal(
            final URI serviceAddress,
            final String serviceName,
            final KeyPair serviceKeyPair) throws InvalidKeyException {
    	return getSignedInstanceNoTerminal(serviceAddress, serviceName, serviceKeyPair, null);
    }
    
    /**
     * Returns a new signed <code>KeyPairingVisualCode</code> instance without terminal fields set.
     * 
	 * @throws InvalidKeyException if an ECDSA signature cannot be created with the supplied key
	 * 	pair.
     */
    public static KeyPairingVisualCode getSignedInstanceNoTerminal(
            final URI serviceAddress,
            final String serviceName,
            final KeyPair serviceKeyPair,
            final byte[] extraData) throws InvalidKeyException {
    	final KeyPairingVisualCode code = new KeyPairingVisualCode();
    	code.serviceAddress = Preconditions.checkNotNullOrEmpty(
    			serviceAddress, "serviceAddress cannot be null or empty");
    	code.serviceName = Preconditions.checkNotNullOrEmpty(
    			serviceName, "serviceName cannot be null or empty");
    	// Add empty terminal details
    	code.terminal = TerminalDetails.getEmptyInstance();
    	code.extraData = extraData;
    	
    	// Add key and sign
    	checkNotNull(serviceKeyPair, "serviceKeyPair cannot be null");
    	code.servicePublicKey = serviceKeyPair.getPublic();
    	try {
			code.sign(serviceKeyPair.getPrivate());
		} catch (SignatureException e) {
			// Re-thrown because this exception indicates that the signature cannot be created
			// using the bytes returned by getBytesToSign, which should only occur as a result of
			// a program error.
			throw new RuntimeException("unable to create signature", e);
		}
        return code;
    }

    @SerializedName("sn") private String serviceName;
    @SerializedName("spk") private PublicKey servicePublicKey;
    @SerializedName("sig")    private byte[] signature;
    @SerializedName("ed") private byte[] extraData;

    private transient byte[] serviceCommitment;

    // no-args constructor for Gson
    private KeyPairingVisualCode() {
        super(TYPE);
    }

    /**
     * @return the name of the service identified by this visual code.
     */
    public String getServiceName() {
        return serviceName;
    }

    public byte[] getServiceCommitment() {
        if (serviceCommitment == null) {
            serviceCommitment = KeyPairing.commitServicePublicKey(servicePublicKey);
        }
        return serviceCommitment;
    }

    /**
     * @return the public key of the service identified by this visual code.
     */
    public PublicKey getServicePublicKey() {
        return servicePublicKey;
    }

    @Override
    public byte[] getSignature() {
        return signature;
    }
    
    public byte[] getExtraData(){
    	return extraData;
    }

    /**
     * Get the bytes to be signed for this <code>KeyPairVisualCode</code>. The fields of the visual
     * code are concatenated in the following order and format:
     * 
     * <p>
     * <code>serviceName</code>||<code>serviceAddress</code>
     * 
     * <p>
     * Where:
     * 
     * <p>
     * <ul>
     * <li><code>serviceName</code> is the service's name (see {@link #getServiceName()}), UTF-8
     * encoded.
     * <li><code>serviceAddress</code> is the service's address (see {@link #getServiceAddress()}),
     * UTF-8 encoded.
     * </ul>
     * 
     * <p>
     * The signature is created using the private key corresponding to the public key included in
     * the visual code (see {@link #getServicePublicKey()}).
     * 
     * @return byte array containing bytes to be signed.
     */
    public byte[] getBytesToSign() {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final Charset utf8 = Charset.forName("UTF-8");
        try {
            os.write(serviceName.getBytes(utf8));
            os.write(serviceAddress.toString().getBytes(utf8));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "ByteArrayOutputStream should never throw an IOException here", e);
        }
        return os.toByteArray();
    }

    private void sign(final PrivateKey servicePrivateKey) 
    		throws InvalidKeyException, SignatureException {
        // Verify the method's preconditions
        checkNotNull(servicePrivateKey, "servicePrivateKey cannot be null");

        final Signature sig = CryptoFactory.INSTANCE.sha256Ecdsa();
        sig.initSign(servicePrivateKey);
        sig.update(getBytesToSign());
        signature = sig.sign();
    }
}
