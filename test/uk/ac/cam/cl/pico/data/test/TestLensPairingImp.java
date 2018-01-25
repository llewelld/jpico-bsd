package uk.ac.cam.cl.pico.data.test;

import java.util.Map;

import uk.ac.cam.cl.pico.data.pairing.LensPairingImp;
import uk.ac.cam.cl.pico.data.service.Service;

public class TestLensPairingImp extends TestPairingImp implements
        LensPairingImp {

    private Map<String, String> credentials;

    TestLensPairingImp(
            String name,
            Service service,
            Map<String, String> credentials) {
        super(name, service);
        this.credentials = credentials;
    }

    @Override
    public Map<String, String> getCredentials() {
        return credentials;
    }

}
