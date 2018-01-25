/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.comms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

import uk.ac.cam.cl.pico.crypto.IContinuousVerifier;
import uk.ac.cam.cl.pico.crypto.ProtocolViolationException;
import uk.ac.cam.cl.pico.crypto.messages.EncPicoReauthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncServiceReauthMessage;

public class SocketContinuousHandler implements Callable<Void> {

    public static final int MAX_MESSAGE_LENGTH = 1024;

    private final Socket socket;
    private final MessageSerializer serializer;
    private final IContinuousVerifier verifier;

    private DataInputStream dis = null;
    private DataOutputStream dos = null;

    public SocketContinuousHandler(
            final Socket socket,
            final MessageSerializer serializer,
            final IContinuousVerifier verifier) {
        this.socket = socket;
        this.serializer = serializer;
        this.verifier = verifier;
    }

    private int readMessageLength() throws IOException {
        final int len = dis.readInt();
        if (len <= 0 || len > MAX_MESSAGE_LENGTH) {
            throw new IOException("Invalid message length");
        } else {
            return len;
        }
    }

    private void writeMessageLength(final int len) throws IOException {
        if (len <= 0 || len > MAX_MESSAGE_LENGTH) {
            throw new IOException("Invalid message length");
        } else {
            dos.writeInt(len);
        }
    }

    @Override
    public Void call() throws EOFException, IOException, ProtocolViolationException {
        try {
            // Open I/O streams
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            while (verifier.getState() == IContinuousVerifier.State.ACTIVE ||
            		verifier.getState() == IContinuousVerifier.State.PAUSED) {
                // Read EncPicoReauthMessage from client:
                byte[] seprm = IOUtils.toByteArray(dis, readMessageLength());
                EncPicoReauthMessage eprm = serializer.deserialize(
                		seprm, EncPicoReauthMessage.class);

                // Pass to verifier and get next message
                EncServiceReauthMessage esrm = verifier.reauth(eprm);

                // Write EncServiceReauthMessage response back to client
                byte[] sesrm = serializer.serialize(esrm, EncServiceReauthMessage.class);
                writeMessageLength(sesrm.length);
                IOUtils.write(sesrm, dos);
                dos.flush();
            }
        } catch (IOException e) {
            // Re-throw for caller to deal with
            throw e;
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
                if (dos != null) {
                    dos.close();
                }
            } catch (IOException e) {
                // Re-throw for caller to deal with
                throw e;
            }
        }
        return null;
    }
}
