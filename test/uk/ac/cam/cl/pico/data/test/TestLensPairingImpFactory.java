package uk.ac.cam.cl.pico.data.test;

import java.util.Map;

import uk.ac.cam.cl.pico.data.pairing.LensPairing;
import uk.ac.cam.cl.pico.data.pairing.LensPairingImp;
import uk.ac.cam.cl.pico.data.pairing.LensPairingImpFactory;
import uk.ac.cam.cl.pico.data.service.Service;

public class TestLensPairingImpFactory implements
        LensPairingImpFactory {

    @Override
    public LensPairingImp getImp(
            String name,
            Service service,
            Map<String, String> credentials) {
        return new TestLensPairingImp(name, service, credentials);
    }

    @Override
    public LensPairingImp getImp(LensPairing credentialPairing) {
        return getImp(
                credentialPairing.getName(),
                credentialPairing.getService(),
                credentialPairing.getCredentials());
    }

}
