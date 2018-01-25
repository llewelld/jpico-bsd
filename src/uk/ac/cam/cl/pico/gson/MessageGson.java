/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.gson;

import java.security.PublicKey;

import uk.ac.cam.cl.pico.crypto.Nonce;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Convenience class which provides a custom {@link com.google.gson.Gson} instance for
 * JSON-serializing the various {@link Message} classes.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
public class MessageGson {

    /**
     * The custom <code>Gson</code> instance.
     */
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(byte[].class, new ByteArrayGsonSerializer())
            .registerTypeAdapter(PublicKey.class, new PublicKeyGsonSerializer())
            .registerTypeAdapter(Nonce.class, new NonceGsonSerializer())
            .disableHtmlEscaping()
            .create();
}
