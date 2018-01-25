package uk.ac.cam.cl.pico.data.test;

import java.security.PrivateKey;
import java.security.PublicKey;

import uk.ac.cam.cl.pico.data.terminal.Terminal;

public class TestTerminalImpFactory implements Terminal.ImpFactory {

	@Override
	public Terminal.Imp getImp(
			String name, byte[] commitment, PublicKey picoPublicKey, PrivateKey picoPrivateKey) {
		return new TestTerminalImp(name, commitment, picoPublicKey, picoPrivateKey);
	}

	@Override
	public Terminal.Imp getImp(Terminal terminal) {
		return getImp(
				terminal.getName(),
				terminal.getCommitment(),
				terminal.getPicoPublicKey(),
				terminal.getPicoPrivateKey());
	}

}
