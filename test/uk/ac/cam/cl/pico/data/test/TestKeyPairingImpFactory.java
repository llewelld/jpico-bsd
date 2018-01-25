package uk.ac.cam.cl.pico.data.test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import uk.ac.cam.cl.pico.data.pairing.KeyPairing;
import uk.ac.cam.cl.pico.data.pairing.KeyPairingImp;
import uk.ac.cam.cl.pico.data.pairing.KeyPairingImpFactory;
import uk.ac.cam.cl.pico.data.service.Service;

public class TestKeyPairingImpFactory implements KeyPairingImpFactory {

    @Override
    public KeyPairingImp getImp(
            String name,
            Service service,
            PublicKey publicKey,
            PrivateKey privateKey) {
        return new TestKeyPairingImp(name, service, publicKey, privateKey);
    }

    @Override
    public KeyPairingImp getImp(String name, Service service, KeyPair keyPair) {
        return getImp(
                name, service, keyPair.getPublic(), keyPair.getPrivate());
    }

    @Override
    public KeyPairingImp getImp(KeyPairing keyPairing) {
        return getImp(
                keyPairing.getName(),
                keyPairing.getService(),
                keyPairing.getPublicKey(),
                keyPairing.getPrivateKey());
    }

}
