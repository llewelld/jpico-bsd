/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.gson;

import java.lang.reflect.Type;
import java.security.PublicKey;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.cam.cl.pico.crypto.Nonce;
import uk.ac.cam.cl.pico.visualcode.DelegatePairingVisualCode;
import uk.ac.cam.cl.pico.visualcode.KeyAuthenticationVisualCode;
import uk.ac.cam.cl.pico.visualcode.KeyPairingVisualCode;
import uk.ac.cam.cl.pico.visualcode.LensAuthenticationVisualCode;
import uk.ac.cam.cl.pico.visualcode.LensPairingVisualCode;
import uk.ac.cam.cl.pico.visualcode.TerminalPairingVisualCode;
import uk.ac.cam.cl.pico.visualcode.VisualCode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Convenience class which provides a custom {@link com.google.gson.Gson} instance for
 * JSON-serializing {@link VisualCode} objects.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
public class VisualCodeGson {

    /**
     * The custom <code>Gson</code> instance.
     */
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(VisualCode.class, new VisualCodeGsonSerializer())
            .registerTypeAdapter(Nonce.class, new NonceGsonSerializer())
            .registerTypeAdapter(byte[].class, new ByteArrayGsonSerializer())
            .registerTypeAdapter(PublicKey.class, new PublicKeyGsonSerializer())
            .disableHtmlEscaping()
            .create();
}

/**
 * Custom Gson serializer and deserializer for {@link VisualCode} subclass instances.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 */
final class VisualCodeGsonSerializer implements JsonSerializer<VisualCode>,
        JsonDeserializer<VisualCode> {

    private static Map<String, Class<? extends VisualCode>> map =
            new TreeMap<String, Class<? extends VisualCode>>();

    static {
        map.put(LensAuthenticationVisualCode.TYPE, LensAuthenticationVisualCode.class);
        map.put(LensPairingVisualCode.TYPE, LensPairingVisualCode.class);
        map.put(KeyPairingVisualCode.TYPE, KeyPairingVisualCode.class);
        map.put(KeyAuthenticationVisualCode.TYPE, KeyAuthenticationVisualCode.class);
        map.put(TerminalPairingVisualCode.TYPE, TerminalPairingVisualCode.class);
        map.put(DelegatePairingVisualCode.TYPE, DelegatePairingVisualCode.class);
    }

    @Override
    public VisualCode deserialize(final JsonElement json, final Type type,
            final JsonDeserializationContext context) throws JsonParseException {

        // Verify whether the VisualCode is a valid type
        final String visualCodeType = json.getAsJsonObject().get("t").getAsString();
        if (!map.containsKey(visualCodeType)) {
            throw new JsonParseException("Invalid visual code type: " + visualCodeType);
        }
        else {
            return context.deserialize(json, map.get(visualCodeType));
        }

    }

    @Override
    public JsonElement serialize(final VisualCode visualCode, final Type type,
            final JsonSerializationContext context) {

        // Verify whether the VisualCode is a valid type
        if (!map.containsKey(visualCode.getType())) {
            throw new JsonParseException("VisualCode type is invalid");
        }
        else {
            return context.serialize(visualCode, map.get(visualCode.getType()));
        }
    }
}