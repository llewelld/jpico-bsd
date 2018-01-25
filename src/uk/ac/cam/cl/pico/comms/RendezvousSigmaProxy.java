/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.comms;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.rendezvous.RendezvousChannel;

public class RendezvousSigmaProxy extends CombinedVerifierProxy {
	
    private final static Logger LOGGER =
            LoggerFactory.getLogger(RendezvousSigmaProxy.class.getSimpleName());
    
	private final RendezvousChannel channel;

	public RendezvousSigmaProxy(RendezvousChannel channel, MessageSerializer serializer) {
		super(serializer);
		this.channel = channel;
	}

	@Override
	protected byte[] getResponse(byte[] serializedMessage) throws IOException {
		writeMessage(serializedMessage);
		return readMessage();
	}
	   
	private void writeMessage(byte[] serializedMessage) throws IOException {
		final DataOutputStream dos = new DataOutputStream(
				new BufferedOutputStream(channel.getOutputStream()));
		
        final int numBytesInMessage = serializedMessage.length;
        LOGGER.debug("Writing serialised message of {} bytes...", numBytesInMessage);
        dos.writeInt(numBytesInMessage);
        IOUtils.write(serializedMessage, dos);
        dos.flush();
        LOGGER.debug("Message written");
	}
	
	private byte[] readMessage() throws IOException {
        // Read the response from the socket
        final DataInputStream dis = new DataInputStream(channel.getInputStream());

        final int numBytesInMessage = dis.readInt();
        LOGGER.debug("Reading serialised message of {} bytes...", numBytesInMessage);
        final byte[] b = IOUtils.toByteArray(dis, numBytesInMessage);
        LOGGER.debug("Message read");
        return b;
    }
}