package uk.ac.cam.cl.pico.data.terminal;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;


public class TerminalTest {

	public static String name(String mod) {
		return "terminal name" + mod;
	}
	
	public static byte[] commitment(String mod) {
		return ("terminal commitment" + mod).getBytes();
	}
	
	public static final String DEFAULT_NAME = name("");
	public static final byte[] DEFAULT_COMMITMENT = commitment("");

	public static Terminal getTerminal(Terminal.ImpFactory factory, String mod) {
        try {
            return new Terminal(
            		factory,
            		name(mod),
            		commitment(mod),
            		CryptoFactory.INSTANCE.ecKpg().generateKeyPair());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception occured while creating Terminal instance", e);
        }
    }
}
