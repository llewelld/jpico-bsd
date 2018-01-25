/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.comms;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import uk.ac.cam.cl.pico.crypto.messages.Message;

/**
 * Interfaces for classes which define a way of serializing and deserializing the various
 * {@link Message} classes.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
public interface MessageSerializer {

    /**
     * Serialize a message of a given type to a byte array.
     * 
     * @param msg the message to be serialized.
     * @param type the type of the message.
     * @return a byte array containing the message in some well-defined format.
     * @throws UnsupportedEncodingException
     */
    public byte[] serialize(Message msg, Type type)
            throws UnsupportedEncodingException;

    /**
     * Deserialize a message of a given type from a byte array.
     * 
     * @param bytes a byte array containing the message in a some well-defined format.
     * @param classOfT the type of the message to be deserialized.
     * @return the deserialized message object.
     * @throws UnsupportedEncodingException
     */
    public <T extends Message> T deserialize(byte[] bytes, Class<T> classOfT)
            throws UnsupportedEncodingException;
}
