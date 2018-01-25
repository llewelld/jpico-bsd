package uk.ac.cam.cl.pico.data.test;

import uk.ac.cam.cl.pico.data.pairing.Pairing;
import uk.ac.cam.cl.pico.data.pairing.PairingImp;
import uk.ac.cam.cl.pico.data.pairing.PairingImpFactory;
import uk.ac.cam.cl.pico.data.service.Service;

public class TestPairingImpFactory implements PairingImpFactory {

    @Override
    public PairingImp getImp(String name, Service service) {
        return new TestPairingImp(name, service);
    }

    @Override
    public PairingImp getImp(Pairing pairing) {
        return getImp(pairing.getName(), pairing.getService());
    }

}
