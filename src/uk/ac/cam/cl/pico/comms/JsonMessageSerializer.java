/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.comms;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import uk.ac.cam.cl.pico.crypto.messages.Message;
import uk.ac.cam.cl.pico.gson.MessageGson;

import com.google.gson.Gson;

/**
 * A <code>MessageSerializer</code> implementation which serializes {@link Message} objects by
 * turning them into JSON.
 * 
 * The JSON strings are encoded to byte arrays using the <code>UTF-8</code> character set.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
public class JsonMessageSerializer implements MessageSerializer {

    private static final Gson gson = MessageGson.gson;

    @Override
    public byte[] serialize(Message m, Type type)
            throws UnsupportedEncodingException {
        String jsonString = gson.toJson(m, type);
        return jsonString.getBytes("UTF-8");
    }

    @Override
    public <T extends Message> T deserialize(byte[] bytes, Class<T> classOfT)
            throws UnsupportedEncodingException {
        String jsonString = new String(bytes, "UTF-8");
        return gson.fromJson(jsonString, classOfT);
    }
}
