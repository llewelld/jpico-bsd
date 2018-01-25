/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A DataInputStream which provides an option to read a byte array which has been prepended by its
 * length in bytes.
 * 
 * NB: Does not need to be Destroyable, as long as the underlying stream source is destroyed.
 * 
 * @author cw471
 * 
 */
public class LengthPrependedDataInputStream extends DataInputStream {

    public final static int maxLength = 200 * 1024; // 200K

    public LengthPrependedDataInputStream(InputStream in) {
        super(in);
    }

    public byte[] readVariableLengthByteArray() throws IOException {
        int length = readInt();
        if (length < 0 || length > maxLength) {
            throw new IOException(String.format(
                    "Invalid length of byte array (%d)", length));
        }
        byte[] bytes = new byte[length];
        readFully(bytes); // May also raise an IOException.
        return bytes;
    }

    public DestroyableByteArray readDestroyableVariableLengthByteArray()
            throws IOException {
        return new DestroyableByteArray(readVariableLengthByteArray());
    }

}
