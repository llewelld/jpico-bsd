/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.gson;

import uk.ac.cam.cl.pico.backup.EncBackupFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Convenience class which provides a custom {@link com.google.gson.Gson} instance for
 * JSON-serializing {@link EncBackupFile} objects.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * 
 */
final public class EncBackupFileGson {

    /**
     * The custom <code>Gson</code> instance.
     */
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(byte[].class, new ByteArrayGsonSerializer())
            .disableHtmlEscaping()
            .create();
}