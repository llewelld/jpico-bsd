/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;

public enum CryptoFactory {
	INSTANCE;
	
	public static final ECGenParameterSpec PRIME192V1 = new ECGenParameterSpec("prime192v1");
	public static final String HMAC_SHA256 = "Hmac-SHA256";
	public static final String SHA256_ECDSA = "SHA256WITHECDSA";
	public static final String AES = "AES";
	public static final String AES_GCM = "AES/GCM/NoPadding";
	
	private final Provider bcProvider;
	
	private CryptoFactory() {
		// Use SpongyCastle is present (overriding the reduced android one), otherwise use
		// BouncyCastle
		Provider provider = Security.getProvider("SC");
		if (provider == null) {
			provider = Security.getProvider("BC");
		}
		bcProvider = checkNotNull(provider, "Neither BouncyCastle nor spongycastle provider found");
	}
	
	public KeyPairGenerator ecKpg() {
		try {
			// Get the KeyPairGenerator
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", bcProvider);

		    // Initialise with the curve specification
		    kpg.initialize(new ECGenParameterSpec("prime192v1"), new SecureRandom());
		    
		    return kpg;
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new CryptoRuntimeException(e);
		}
	}
	
	public KeyAgreement ecKeyAgreement() {
		try {
			return KeyAgreement.getInstance("ECDH", bcProvider);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public MessageDigest sha256() {
	    try {
    	return MessageDigest.getInstance("SHA-256");
    	} catch (NoSuchAlgorithmException e) {
            throw new CryptoRuntimeException(e);
        }
	}
	
	public Mac sha256Hmac() {
		try {
			return Mac.getInstance(HMAC_SHA256, bcProvider);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}
	
	public Signature sha256Ecdsa() {
		try {
			return Signature.getInstance(SHA256_ECDSA, bcProvider);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}
	   
	public KeyGenerator aes256Kg() {
		try {
			KeyGenerator kg = KeyGenerator.getInstance(AES, bcProvider);
			kg.init(256);
			return kg;
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}
	
	public Cipher aes256() {
		try {
			return Cipher.getInstance(AES_GCM, bcProvider);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public KeyGenerator sha256HmacKg() {
		try {
			return KeyGenerator.getInstance(HMAC_SHA256, bcProvider);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public KeyFactory ecKeyFactory() {
		try {
			return KeyFactory.getInstance("EC", bcProvider);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}
}
