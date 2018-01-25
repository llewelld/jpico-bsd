/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.comms;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

import uk.ac.cam.cl.pico.crypto.ISigmaVerifier;
import uk.ac.cam.cl.pico.crypto.ProtocolViolationException;
import uk.ac.cam.cl.pico.crypto.messages.EncPicoAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncServiceAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncStatusMessage;
import uk.ac.cam.cl.pico.crypto.messages.StartMessage;
import uk.ac.cam.cl.rendezvous.RendezvousChannel;

public class RendezvousSigmaHandler extends AbstractHandler implements Callable<Void> {

    public static final int MAX_MESSAGE_LENGTH = 1000000; ///10240;

    private final MessageSerializer serializer;
    private final ISigmaVerifier verifier;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean shouldContinue;
    private final RendezvousChannel channel;

    public RendezvousSigmaHandler(
            final RendezvousChannel channel,
            final MessageSerializer serializer,
            final ISigmaVerifier verifier) {
    	//don't want to upset all existing Rendezvous code, so just assume not continuous authentication for now
    	super(serializer, verifier, false);
        this.serializer = serializer;
        this.verifier = verifier;
        shouldContinue = true;
        
        this.channel = channel;

        // Open I/O streams
        dis = new DataInputStream(channel.getInputStream());
        dos = new DataOutputStream(new BufferedOutputStream(channel.getOutputStream()));
    }

    private int readMessageLength() throws IOException {
        final int len = dis.readInt();
        if (len <= 0 || len > MAX_MESSAGE_LENGTH) {
            throw new IOException("Invalid message length " + len + " (max=" + MAX_MESSAGE_LENGTH + ")");
        } else {
            return len;
        }
    }

    private void writeMessageLength(final int len) throws IOException {
        if (len <= 0 || len > MAX_MESSAGE_LENGTH) {
            throw new IOException("Invalid message length " + len + " (max=" + MAX_MESSAGE_LENGTH + ")");
        } else {
            dos.writeInt(len);
        }
    }
    
    @Override
    public Void call() throws IOException, ProtocolViolationException {
    	try {
    		if (shouldContinue) {
	            // FIRST ROUND-TRIP:
	            // Read StartMessage from client
	            final byte[] ssm = IOUtils.toByteArray(dis, readMessageLength());
	    		if (!shouldContinue) {
	    			// Abort, abort!!
	    			return null;
	    			// Surely there's a nice way?
	    		}

	            final StartMessage sm = serializer.deserialize(ssm, StartMessage.class);
	
	            // Pass to verifier and get next message
	            final EncServiceAuthMessage esam = verifier.start(sm);
	
	            // Write EncServiceAuthMessage response back to client
	            final byte[] sesam = serializer.serialize(
	                    esam, EncServiceAuthMessage.class);

	            writeMessageLength(sesam.length);
	            IOUtils.write(sesam, dos);
	            dos.flush();
	    		if (!shouldContinue) {
	    			// Abort, abort!!
	    			return null;
	    		}

	            // SECOND ROUND-TRIP:
	            // Read EncAuthMessage from client:
	            final byte[] seam = IOUtils.toByteArray(dis, readMessageLength());

	            final EncPicoAuthMessage eam = serializer.deserialize(
	                    seam, EncPicoAuthMessage.class);
	
	            // Pass to verifier and get next message
	            final EncStatusMessage esm = verifier.authenticate(eam);
	
	            // Write EncStatusMessage response back to client
	            final byte[] sesm = serializer.serialize(esm, EncStatusMessage.class);
	            writeMessageLength(sesm.length);
	            IOUtils.write(sesm, dos);
	            dos.flush();
    		}
        } catch (IOException e) {
            // Re-throw for caller to deal with
            throw e;
        } catch (ProtocolViolationException e) {
			// Re-throw for caller to deal with
			throw e;
		} finally {
            try {
                dis.close();
                dos.close();
            } catch (IOException e) {
                // Re-throw for caller to deal with
                throw e;
            }
        }
		return null;
    }
    
    public void abort() {
    	shouldContinue = false;
    }

protected DataInputStream getInputStream() throws IOException {
		if (dis == null) {
			dis = new DataInputStream(channel.getInputStream());;
		}
		return dis;
	}

	@Override
	protected DataOutputStream getOutputStream() throws IOException {
		if (dos == null) {
			// Why is this BufferedOutputStream here?
			dos = new DataOutputStream(new BufferedOutputStream(channel.getOutputStream()));
		}
		return dos;
	}
}