package uk.ac.cam.cl.pico.util;

import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;

import uk.ac.cam.cl.pico.config.Config;

@Deprecated
public class KgHelper {

    public static KeyGenerator getKg() throws NoSuchAlgorithmException, NoSuchProviderException {
        Config config = Config.getInstance();
        String kgAlgorithm;
        String provider;

        if ((kgAlgorithm = (String) config.get("crypto.kg_algorithm")) == null) {
            throw new InvalidParameterException("crypto.kg_algorithm cannot be null");
        }
        if ((provider = (String) config.get("crypto.provider")) == null) {
            throw new InvalidParameterException("crypto.provider cannot be null");
        }

        return KeyGenerator.getInstance(kgAlgorithm, provider);
    }
}
