/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.comms;

import java.io.IOException;

import uk.ac.cam.cl.pico.crypto.ICombinedVerifier;
import uk.ac.cam.cl.pico.crypto.messages.EncPicoAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncPicoReauthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncServiceAuthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncServiceReauthMessage;
import uk.ac.cam.cl.pico.crypto.messages.EncStatusMessage;
import uk.ac.cam.cl.pico.crypto.messages.StartMessage;

/**
 * Abstract base class for proxies of the interfaces of remote Pico services.
 * 
 * Subclasses must provide an implementation of <code>getResponse</code> and must call the parent
 * constructor with a {@link MessageSerializer}.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * @deprecated use {@link CombinedVerifierProxy} instead.
 * 
 */
@Deprecated
public abstract class ProxyService implements ICombinedVerifier {

    private final MessageSerializer serializer;

    protected ProxyService(MessageSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * Subclasses can override lazyInit to do work before the first message is sent, but after the
     * Service is constructed. For example, opening a TCP socket
     */
    protected void lazyInit() throws IOException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.ac.cam.cl.pico.comms.ServiceInterface2#startSession(uk.ac.cam.cl.pico.crypto.messages.
     * StartMessage)
     */
    @Override
    public final EncServiceAuthMessage start(StartMessage msg)
            throws IOException {
        byte[] serializedStartMessage =
                serializer.serialize(msg, StartMessage.class);
        lazyInit();
        byte[] serializedEncServiceAuthMessage =
                sendRequest(serializedStartMessage);
        return serializer.deserialize(serializedEncServiceAuthMessage,
                EncServiceAuthMessage.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.ac.cam.cl.pico.comms.ServiceInterface2#authenticate(uk.ac.cam.cl.pico.crypto.messages.
     * EncAuthMessage)
     */
    @Override
    public final EncStatusMessage authenticate(EncPicoAuthMessage msg)
            throws IOException {
        byte[] serializedEncAuthMessage =
                serializer.serialize(msg, EncPicoAuthMessage.class);
        byte[] response = sendRequest(serializedEncAuthMessage);
        return serializer.deserialize(response,
                EncStatusMessage.class);
    }

    @Override
    public final EncServiceReauthMessage reauth(
            EncPicoReauthMessage msg) throws IOException {
        byte[] serializedEncReauthRequestMsg =
                serializer.serialize(msg,
                        EncPicoReauthMessage.class);
        byte[] response = sendRequest(serializedEncReauthRequestMsg);
        return serializer
                .deserialize(response, EncServiceReauthMessage.class);
    }

    protected abstract byte[] sendRequest(byte[] serializedMessage)
            throws IOException;

}
