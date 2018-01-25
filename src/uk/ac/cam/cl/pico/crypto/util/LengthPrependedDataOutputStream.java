/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LengthPrependedDataOutputStream extends DataOutputStream {

    public LengthPrependedDataOutputStream(OutputStream out) {
        super(out);
    }

    public void writeVariableLengthByteArray(byte[] b) throws IOException {
        checkNotNull(b, "b cannot be null");
        if (b.length > LengthPrependedDataInputStream.maxLength)
            throw new IOException("Byte array too large " +  b.length + " (max=" + LengthPrependedDataInputStream.maxLength + ")");
        writeInt(b.length);
        write(b);
    }
}
