/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.util;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

public class DestroyableByteArray implements Destroyable {

    private final byte[] array;
    private boolean isDestroyed = false;

    public DestroyableByteArray(byte[] bytes) {
        this.array = bytes;
    }

    public LengthPrependedDataInputStream getLengthPrependedDataInputStream() {
        ByteArrayInputStream is = new ByteArrayInputStream(array); // Does not
                                                                   // copy array
        return new LengthPrependedDataInputStream(is);
    }

    @Override
    public void destroy() throws DestroyFailedException {
        byte zero = 0;
        Arrays.fill(array, zero);
        this.isDestroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return this.isDestroyed;
    }

}
