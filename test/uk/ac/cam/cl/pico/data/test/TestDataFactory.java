package uk.ac.cam.cl.pico.data.test;

import java.net.URI;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.data.DataFactory;
import uk.ac.cam.cl.pico.data.pairing.LensPairing;
import uk.ac.cam.cl.pico.data.pairing.LensPairingImp;
import uk.ac.cam.cl.pico.data.pairing.LensPairingImpFactory;
import uk.ac.cam.cl.pico.data.pairing.KeyPairing;
import uk.ac.cam.cl.pico.data.pairing.KeyPairingImp;
import uk.ac.cam.cl.pico.data.pairing.KeyPairingImpFactory;
import uk.ac.cam.cl.pico.data.pairing.Pairing;
import uk.ac.cam.cl.pico.data.pairing.PairingImp;
import uk.ac.cam.cl.pico.data.pairing.PairingImpFactory;
import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.service.ServiceImp;
import uk.ac.cam.cl.pico.data.service.ServiceImpFactory;
import uk.ac.cam.cl.pico.data.session.Session;
import uk.ac.cam.cl.pico.data.session.Session.Error;
import uk.ac.cam.cl.pico.data.session.Session.Status;
import uk.ac.cam.cl.pico.data.session.SessionImp;
import uk.ac.cam.cl.pico.data.session.SessionImpFactory;
import uk.ac.cam.cl.pico.data.terminal.Terminal;
import uk.ac.cam.cl.pico.data.terminal.Terminal.Imp;

public class TestDataFactory implements DataFactory {

    private ServiceImpFactory sf = new TestServiceImpFactory();
    private PairingImpFactory pf = new TestPairingImpFactory();
    private KeyPairingImpFactory kpf = new TestKeyPairingImpFactory();
    private LensPairingImpFactory cpf = new TestLensPairingImpFactory();
    private SessionImpFactory snf = new TestSessionImpFactory();
    private Terminal.ImpFactory tf = new TestTerminalImpFactory();

    @Override
    public ServiceImp getImp(String name, URI address, byte[] commitment) {
        return sf.getImp(name, address, commitment);
    }

    @Override
    public ServiceImp getImp(Service service) {
        return sf.getImp(service);
    }

    @Override
    public PairingImp getImp(String name, Service service) {
        return pf.getImp(name, service);
    }

    @Override
    public PairingImp getImp(Pairing pairing) {
        return pf.getImp(pairing);
    }

    @Override
    public KeyPairingImp getImp(
            String name,
            Service service,
            PublicKey publicKey,
            PrivateKey privateKey) {
        return kpf.getImp(name, service, publicKey, privateKey);
    }

    @Override
    public KeyPairingImp getImp(String name, Service service, KeyPair keyPair) {
        return kpf.getImp(name, service, keyPair);
    }

    @Override
    public KeyPairingImp getImp(KeyPairing keyPairing) {
        return kpf.getImp(keyPairing);
    }

    @Override
    public LensPairingImp getImp(
            String name,
            Service service,
            Map<String, String> credentials) {
        return cpf.getImp(name, service, credentials);
    }

    @Override
    public LensPairingImp getImp(LensPairing lensPairing) {
        return cpf.getImp(lensPairing);
    }

    @Override
    public SessionImp getImp(
            String remoteId,
            SecretKey secretKey,
            Pairing pairing,
            AuthToken authToken,
            Date lastAuthDate,
            Status status,
            Error error) {
        return snf.getImp(remoteId, secretKey, pairing, authToken, lastAuthDate, status, error);
    }

    @Override
    public SessionImp getImp(Session session) {
        return snf.getImp(session);
    }

	@Override
	public Imp getImp(
			String name, byte[] commitment, PublicKey picoPublicKey, PrivateKey picoPrivateKey) {
		return tf.getImp(name, commitment, picoPublicKey, picoPrivateKey);
	}

	@Override
	public Imp getImp(Terminal terminal) {
		return tf.getImp(terminal);
	}

}
