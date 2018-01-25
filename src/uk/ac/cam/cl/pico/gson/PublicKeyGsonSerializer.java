/**
 * Copyright Pico project, 2016
 */

// Copyright University of Cambridge, 2013

package uk.ac.cam.cl.pico.gson;

import java.lang.reflect.Type;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import uk.ac.cam.cl.pico.crypto.CryptoFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Custom Gson serializer and deserializer for {@link PublicKey} instances.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 */
final class PublicKeyGsonSerializer
        implements JsonSerializer<PublicKey>, JsonDeserializer<PublicKey> {

    private static final KeyFactory kf = CryptoFactory.INSTANCE.ecKeyFactory();

    @Override
    public PublicKey deserialize(
    		final JsonElement json, final Type type, final JsonDeserializationContext context)
    				throws JsonParseException {
        byte[] keyBytes = context.deserialize(json, byte[].class);
        try {
            return kf.generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (InvalidKeySpecException e) {
            throw new JsonParseException(e);
        }

    }

    @Override
    public JsonElement serialize(
    		final PublicKey key, final Type type, final JsonSerializationContext context) {
        return context.serialize(key.getEncoded(), byte[].class);
    }
}
