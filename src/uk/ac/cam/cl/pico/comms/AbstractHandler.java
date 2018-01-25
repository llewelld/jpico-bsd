package uk.ac.cam.cl.pico.comms;

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

/**
 * Abstract base class for handlers which use Input/OutputStreams.
 * 
 * @author Alex Dalgleish <amd96@cam.ac.uk>
 * @author Max Spencer <ms955@cam.ac.uk>
 *
 */
public abstract class AbstractHandler implements Callable<Void> {

    public static final int MAX_MESSAGE_LENGTH = 1000000;
	
	private final MessageSerializer serializer;
	private final ISigmaVerifier verifier;
	private final boolean continuous;
	
	public AbstractHandler(MessageSerializer serializer, ISigmaVerifier verifier, boolean continuous) {
		this.serializer = serializer;
		this.verifier = verifier;
		this.continuous = continuous;
	}
	
	protected abstract DataInputStream getInputStream() throws IOException;
	protected abstract DataOutputStream getOutputStream() throws IOException;
	
	public ISigmaVerifier getVerifier() {
		return verifier;
	}

    private int readMessageLength() throws IOException {
        final int len = getInputStream().readInt();
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
            getOutputStream().writeInt(len);
        }
    }
    
    protected void finish() throws IOException {
    	try {
            if (getInputStream() != null) {
                getInputStream().close();
            }
            if (getOutputStream() != null) {
                getOutputStream().close();
            }
        } catch (IOException e) {
            // Re-throw for caller to deal with
            throw e;
        }
    }

	@Override
	public Void call() throws IOException, ProtocolViolationException {
		try {
            // Open I/O streams
            final DataInputStream dis = getInputStream();
            final DataOutputStream dos = getOutputStream();

            // FIRST ROUND-TRIP:
            // Read StartMessage from client
            final byte[] ssm = IOUtils.toByteArray(dis, readMessageLength());
            final StartMessage sm = serializer.deserialize(ssm, StartMessage.class);

            // Pass to verifier and get next message
            final EncServiceAuthMessage esam = verifier.start(sm);

            // Write EncServiceAuthMessage response back to client
            final byte[] sesam = serializer.serialize(
                    esam, EncServiceAuthMessage.class);
            writeMessageLength(sesam.length);
            IOUtils.write(sesam, dos);
            dos.flush();

            // SECOND ROUND-TRIP:
            // Read EncAuthMessage from client:
            final byte[] seam = IOUtils.toByteArray(dis, readMessageLength());
            final EncPicoAuthMessage eam = serializer.deserialize(seam, EncPicoAuthMessage.class);

            // Pass to verifier and get next message
            final EncStatusMessage esm = verifier.authenticate(eam);

            // Write EncSessionDelegationMessage response back to client
            final byte[] sesdm = serializer.serialize(esm, EncStatusMessage.class);
            writeMessageLength(sesdm.length);
            IOUtils.write(sesdm, dos);
            dos.flush();
        } catch (IOException e) {
            // Re-throw for caller to deal with
            throw e;
        } catch (ProtocolViolationException e) {
			// Re-throw for caller to deal with
			throw e;
		} finally {
			if (!continuous) {
	            finish();
			}
        }
        return null;
	}

}
