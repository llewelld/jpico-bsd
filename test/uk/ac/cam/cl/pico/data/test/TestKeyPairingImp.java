package uk.ac.cam.cl.pico.data.test;

import java.security.PrivateKey;
import java.security.PublicKey;

import uk.ac.cam.cl.pico.data.pairing.KeyPairingImp;
import uk.ac.cam.cl.pico.data.service.Service;

public class TestKeyPairingImp extends TestPairingImp implements
        KeyPairingImp {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    TestKeyPairingImp(
            String name,
            Service service,
            PublicKey publicKey,
            PrivateKey privateKey) {
        super(name, service);
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

}
