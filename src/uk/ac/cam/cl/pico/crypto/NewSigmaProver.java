/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

import uk.ac.cam.cl.pico.crypto.messages.EncPicoAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncServiceAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncStatusMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncryptedMessage.FieldDeserializationException;
import uk.ac.cam.cl.pico.crypto.messages.PicoAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.ServiceAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.StartMessage;
import uk.ac.cam.cl.pico.crypto.messages.StatusMessage;
import uk.ac.cam.cl.pico.data.pairing.KeyPairing;

public class NewSigmaProver {
	
	public static final byte VERSION_1_1 = (byte) 2;
	
	public static class VerifierAuthFailedException extends Exception {

		private static final long serialVersionUID = 1034416744954322551L;

		public VerifierAuthFailedException() {
			super();
		}

		public VerifierAuthFailedException(
				String message,
				Throwable cause) {
			super(message, cause);
		}

		public VerifierAuthFailedException(String message) {
			super(message);
		}

		public VerifierAuthFailedException(Throwable cause) {
			super(cause);
		}
	}
	
	public static class ProverAuthRejectedException extends Exception {

		private static final long serialVersionUID = 4571407415529553097L;

		public ProverAuthRejectedException() {
			super();
		}

		public ProverAuthRejectedException(String message, Throwable cause) {
			super(message, cause);
		}

		public ProverAuthRejectedException(String message) {
			super(message);
		}

		public ProverAuthRejectedException(Throwable cause) {
			super(cause);
		}
	}
	
	private static enum State {
		INITIAL("before authentication"),
		OK("after authentication was successful"),
		FAIL("after authentication failed");
		
		private final String when;
		
		private State(String when) {
			this.when = when;
		}

		public String when() {
			return when;
		}
	}
	
	private State state = State.INITIAL;
	
	// Variables with names prefixed with 'p' are for values related to the prover and those
	// prefixed with 'v' are for values related to the verifier.
	
	// Initial variables:
	private final byte pVersion;
	private final KeyPair pIdKeyPair;
	private final byte[] pExtraData;
	private final KeyPair pEphemKeyPair;
	private final Nonce pNonce;
	private ISigmaVerifier verifier;
	private final byte[] vCommit;
	
	// Output variables:
	private int vSessionId; // getVerifierSessionId
	private SecretKey sharedKey; // getSharedKey
	private byte status; // getStatus
	private byte[] vExtraData; // getReceivedExtraData

	public NewSigmaProver(
			byte proverVersion,
			KeyPair proverIdKeyPair,
			byte[] extraData,
			ISigmaVerifier verifier,
			byte[] verifierCommit) {
		this.pVersion = proverVersion;
		this.pIdKeyPair = checkNotNull(proverIdKeyPair, "proverIdKeyPair cannot be null");
		this.pExtraData = extraData;
		this.verifier = checkNotNull(verifier, "verifier cannot be null");
		this.vCommit = checkNotNull(verifierCommit, "verifierCommit cannot be null");
		
		// Generate ephemeral prover key pair and nonce
		pEphemKeyPair = CryptoFactory.INSTANCE.ecKpg().generateKeyPair();
		pNonce = Nonce.getRandomInstance();
	}
	
	/**
	 * Carry out the SIGMA protocol with the verifier and store the outputs.
	 * 
	 * @return <code>true</code> if the authentication was successful and the verifier is
	 * 	expecting further communication or <code>false</code> if the authentication was successful
	 * 	but no further communication is expected. If the authentication was unsuccessful an 
	 * 	exception is raised.
	 * @throws IOException	if a communication error occurred.
	 * @throws ProverAuthRejectedException if the prover authentication was rejected by the
	 * 	verifier.
	 * @throws ProtocolViolationException if the verifier violated the protocol.
	 * @throws VerifierAuthFailedException if the verifier failed to authenticate to the prover.
	 */
	public boolean prove() 
			throws IOException, ProverAuthRejectedException, ProtocolViolationException, 
			VerifierAuthFailedException {
		if (state == State.INITIAL) {
			// FIRST ROUND-TRIP:
			// The prover creates a Start Message and sends it to the verifier and waits for its
			// response (an encrypted Verifier Auth Message).
	        final StartMessage startMessage =
	        		StartMessage.getInstance(pVersion, pEphemKeyPair.getPublic(), pNonce);
            final EncServiceAuthMessage encServiceAuthMessage = 
            		verifier.start(startMessage); // blocks
 
            // Retrieve and check the cleartext items from the partly-encrypted message.
            final Nonce vNonce;
            final PublicKey vEphemPubKey;
            try {
            	vSessionId = checkNotNull(
            			encServiceAuthMessage.getSessionId(),
            			"verifier session ID missing");
            	vNonce = checkNotNull(
            			encServiceAuthMessage.getServiceNonce(),
            			"verifier nonce missing");
            	vEphemPubKey = checkNotNull(
            			encServiceAuthMessage.getServiceEphemeralPublicKey(),
            			"verifier ephemeral public key missing");
            } catch (NullPointerException e) {
            	state = State.FAIL;
            	throw new ProtocolViolationException(e);
            }
        	
            
            
            // KEY DERIVATION:
            // Using the verifier's ephemeral public key and the prover's private ephemeral key, do
        	// the Diffie-Hellman key agreement (ECDH). This generates a shared secret which is
        	// used as a seed for the key derivarion procedure.
            final KeyAgreement ka = CryptoFactory.INSTANCE.ecKeyAgreement();
            try {
				ka.init(pEphemKeyPair.getPrivate());
			} catch (InvalidKeyException e) {
				// Re-thrown unchecked because this signals incompatibility between the chosen key 
				// pair generation and key agreement algorithms.
				throw new CryptoRuntimeException(e);
			}
            try {
				ka.doPhase(vEphemPubKey, true);
			} catch (InvalidKeyException e) {
				state = State.FAIL;
				throw new ProtocolViolationException(
						"verifier supplied invalid ephemeral public key", e);
			} catch (IllegalStateException e) {
				// Re-thrown unchecked because this block should be unreachable. The key agreement
				// is initialised above.
				throw new RuntimeException(e);
			}
            final byte[] ecdhSharedSecret = ka.generateSecret();
            
            // Carry out the key derivation procedure using the shared secret created above and the
            // two nonces. See the key deriver classes for more information on the key derivation
            // procedure.
            final KeyDeriver kd = SigmaKeyDeriver.getInstance(ecdhSharedSecret, pNonce, vNonce);
            SecretKey pMacKey = kd.getNextKey("Hmac-SHA256", 256);
            SecretKey pEncKey = kd.getNextKey("AES", 128);
            SecretKey vMacKey = kd.getNextKey("Hmac-SHA256", 256);
            SecretKey vEncKey = kd.getNextKey("AES", 128);
            sharedKey = kd.getNextKey("AES", 128);
            kd.destroy();

            // Decrypt the Verifier Auth Message using the "verifier encryption" derived key.
            final ServiceAuthMessage verifierAuthMessage;
            try {
				verifierAuthMessage = encServiceAuthMessage.decrypt(vEncKey);
			} catch (InvalidKeyException e) {
				// Re-thrown unchecked because this signals incompatibility between the chosen 
				// symmetric key generation algorithm of the key deriver and the message cipher
				// scheme
				throw new CryptoRuntimeException(e);
			} catch (InvalidAlgorithmParameterException e) {
				state = State.FAIL;
				throw new ProtocolViolationException("verifier supplied invalid IV", e);
			} catch (IllegalBlockSizeException e) {
				state = State.FAIL;
				throw new ProtocolViolationException(
						"verifier supplied invalid encrypted data", e);
			} catch (BadPaddingException e) {
				state = State.FAIL;
				throw new ProtocolViolationException(
						"verifier supplied invalid encrypted data", e);
			} catch (FieldDeserializationException e) {
				state = State.FAIL;
				throw new ProtocolViolationException(
						"verifier supplied invalid encrypted data", e);
			}

            
            
            // VERIFIER AUTHENTICATION:
            // Having decrypted the Verifier Auth Message, the prover is now in a position to check
            // the verifier's identity. Three checks are carried out:
            // 1. Check the long-term identity (public key) presented by the verifier matches the
            //    commitment the prover was initialised with.
            // 2. Verify the signature in the message using the presented long-term identity public
            //    key. This checks the verifier possesses the corresponding private key. The data
            //    signed includes the prover nonce sent in the Start Message.
            // 3. Check the MAC in the message. The message must include a keyed HMAC of the 
            //    verifier's long-term identity public key. The key for this HMAC is derived from
            //    the ECDH shared secret, making it unforgeble for a MITM attempting an identity
            //    misbinding attack.
            final PublicKey vIdPubKey = verifierAuthMessage.getServicePublicKey();
            
            // 1. Check the long-term identity (public key) presented by the verifier matches the
            //    commitment the prover was initialised with:
            final byte[] vPresentedCommit = KeyPairing.commitServicePublicKey(vIdPubKey);
            if (!Arrays.equals(vCommit, vPresentedCommit)) {
            	state = State.FAIL;
            	throw new VerifierAuthFailedException(
            			"identity presented by verifier did not match prior commitment");
            }
            
            // 2. Verify the signature in the message using the long-term identity public key:
            final Signature sig = CryptoFactory.INSTANCE.sha256Ecdsa();
            try {
				sig.initVerify(vIdPubKey);
	            sig.update(ServiceAuthMessage.getBytesToSign(pNonce, vSessionId, vEphemPubKey));
	            if (!sig.verify(verifierAuthMessage.getSignature())) {
	            	state = State.FAIL;
	            	throw new VerifierAuthFailedException(
	            			"invalid signature presented by verifier");
	            }
			} catch (InvalidKeyException e) {
				// The public key presented by the verifier was not of the correct type to have 
				// created the signature.
				state = State.FAIL;
				throw new ProtocolViolationException(
						"verifier supplied invalid identity public key", e);
			} catch (SignatureException e) {
				// Verification of the signature was not possible. This is more serious than the 
				// signature being invalid, i.e. not corresponding to the presented public key.
				state = State.FAIL;
				throw new ProtocolViolationException(
						"unable to verify signature presented by verifier", e);
			}
                        
            // 3. Check the MAC in the message:
            final Mac mac = CryptoFactory.INSTANCE.sha256Hmac();
            try {
				mac.init(vMacKey);
			} catch (InvalidKeyException e) {
				// Re-thrown unchecked because this signals incompatibility between the key
				// generation algorithm used by the key deriver and the algorithm used to produce
				// the MAC.
				throw new CryptoRuntimeException(
						"invalid verifier MAC key returned by key deriver", e);
			}
            final byte[] vExpectedMac = mac.doFinal(vIdPubKey.getEncoded());
            if (!Arrays.equals(vExpectedMac, verifierAuthMessage.getMac())) {
            	state = State.FAIL;
            	throw new VerifierAuthFailedException("invalid MAC presented by verifier");
            }
            
            
            
            // SECOND ROUND TRIP:
            // Prover creates the Prover Authentication Message, encrypts it using the derived 
            // "prover encryption" key, sends it to the verifier and waits for the response (an 
            // encrypted Status Message).
            final PicoAuthMessage picoAuthMessage;
            try {
            	picoAuthMessage = PicoAuthMessage.getInstance(
            			vSessionId,
            			vNonce,
            			pEphemKeyPair.getPublic(),
            			pIdKeyPair,
            			pMacKey,
            			pExtraData);
            } catch (SignatureException e) {
            	// Re-thrown unchecked because this signals that the signature algorithm has been
            	// unable to process the supplied data to sign. This is considered an unrecoverable
            	// error, probably resulting from incompatibility between the chosen crypto
            	// algorithms.
            	throw new CryptoRuntimeException(
            			"unable to create prover auth message signature", e);
            } catch (InvalidKeyException e) {
            	// Re-thrown unchecked because this signals incompatibility between the key
				// generation algorithm used by the key deriver and the algorithm used to produce
				// the MAC, or incompatibility between the key pair generation algorhtm used for
            	// the prover's long-term identity and the chosen signature algorithm.
				throw new CryptoRuntimeException(
						"invalid prover MAC key returned by key deriver", e);
			}
            
            // Encrypt using the appropriate derived key
            EncPicoAuthMessage encPicoAuthMessage;
			try {
				encPicoAuthMessage = picoAuthMessage.encrypt(pEncKey);
			} catch (InvalidKeyException e) {
				// Re-thrown unchecked because this signals incompatibility between the chosen 
				// symmetric key generation algorithm of the key deriver and the message cipher
				// scheme
				throw new CryptoRuntimeException(e);
			}
			
			// Get reply from verifier and decrypt using appropriate derived key
	        final EncStatusMessage encStatusMessage =
	        		verifier.authenticate(encPicoAuthMessage); // blocks
	        StatusMessage statusMessage;
			try {
				statusMessage = encStatusMessage.decrypt(vEncKey);
			} catch (InvalidKeyException e) {
				// Re-thrown unchecked because this signals incompatibility between the chosen 
				// symmetric key generation algorithm of the key deriver and the message cipher
				// scheme
				throw new CryptoRuntimeException(e);
			} catch (InvalidAlgorithmParameterException e) {
				state = State.FAIL;
				throw new ProtocolViolationException("verifier supplied invalid IV", e);
			} catch (IllegalBlockSizeException e) {
				state = State.FAIL;
				throw new ProtocolViolationException(
						"verifier supplied invalid encrypted data", e);
			} catch (BadPaddingException e) {
				state = State.FAIL;
				throw new ProtocolViolationException(
						"verifier supplied invalid encrypted data", e);
			} catch (FieldDeserializationException e) {
				state = State.FAIL;
				throw new ProtocolViolationException(
						"verifier supplied invalid encrypted data", e);
			}
	        
	        
	        
	        // CHECK STATUS AND RETURN:
	        // Retrieve and save the status byte returned in the status message and take some
	        // action depending on its value.
	        status = statusMessage.getStatus();
	        if (status == StatusMessage.OK_DONE || status == StatusMessage.OK_CONTINUE) {
	        	// Successfuly authenticated
	        	vExtraData = statusMessage.getExtraData();
	        	state = State.OK;
	        	// Return true if verifier is expecting further communication (OK_CONTINUE) or
	        	// false otherwise.
	        	return (status == StatusMessage.OK_CONTINUE);
	        } else if (status == StatusMessage.REJECTED) {
	        	state = State.FAIL;
	        	throw new ProverAuthRejectedException();
	        } else {
	        	state = State.FAIL;
	        	throw new ProtocolViolationException("invalid status byte");
	        }
		} else {
			throw new IllegalStateException("invalid operation " + state.when());
		}
	}
	
	public int getVerifierSessionId() {
		if (state == State.OK) {
			return vSessionId;
		} else {
			throw new IllegalStateException("invalid operation " + state.when());
		}
	}
	
	public SecretKey getSharedKey() {
		if (state == State.OK) {
			return sharedKey;
		} else {
			throw new IllegalStateException("invalid operation " + state.when());
		}
	}
	
	public byte getStatus() {
		if (state == State.OK){
			return status;
		} else {
			throw new IllegalStateException("invalid operation " + state.when());
		}
	}
	
	public byte[] getReceivedExtraData() {
		if (state == State.OK) {
			return vExtraData;
		} else {
			throw new IllegalStateException("invalid operation " + state.when());
		}
	}
}