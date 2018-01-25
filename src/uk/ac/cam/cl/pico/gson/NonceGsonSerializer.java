/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.gson;

import java.lang.reflect.Type;

import uk.ac.cam.cl.pico.crypto.Nonce;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Custom Gson serializer and deserializer for {@link Nonce} instances.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 */
final class NonceGsonSerializer implements JsonSerializer<Nonce>,
        JsonDeserializer<Nonce> {

    @Override
    public Nonce deserialize(final JsonElement json, final Type type,
            final JsonDeserializationContext context) throws JsonParseException {

        final byte[] valueBytes = context.deserialize(json, byte[].class);
        return Nonce.getInstance(valueBytes);
    }

    @Override
    public JsonElement serialize(final Nonce nonce, final Type type,
            final JsonSerializationContext context) {

        if (nonce.isDestroyed()) {
            throw new IllegalStateException(
                    "A destroyed Nonce can't be serialized");
        }

        return context.serialize(nonce.getValue(), byte[].class);
    }

}
