package uk.ac.cam.cl.pico.data.test;

import java.net.URI;

import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.service.ServiceImp;
import uk.ac.cam.cl.pico.data.service.ServiceImpFactory;

public class TestServiceImpFactory implements ServiceImpFactory {

    @Override
    public ServiceImp getImp(String name, URI address, byte[] commitment) {
        return new TestServiceImp(name, address, commitment);
    }

    @Override
    public ServiceImp getImp(Service service) {
        return getImp(
                service.getName(),
                service.getAddress(),
                service.getCommitment());
    }

}
