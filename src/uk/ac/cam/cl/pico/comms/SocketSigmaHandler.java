/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.comms;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import uk.ac.cam.cl.pico.crypto.ISigmaVerifier;

public class SocketSigmaHandler extends AbstractHandler {

	private final Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public SocketSigmaHandler(final Socket socket, final MessageSerializer serializer, final ISigmaVerifier verifier, final boolean continuous) {
    	super(serializer, verifier, continuous);
    	this.socket = socket;
    }
    
	@Override
	protected DataInputStream getInputStream() throws IOException {
		if (dis == null) {
			dis = new DataInputStream(socket.getInputStream());;
		}
		return dis;
	}

	@Override
	protected DataOutputStream getOutputStream() throws IOException {
		if (dos == null) {
			// Why is this BufferedOutputStream here?
			dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		}
		return dos;
	}
}