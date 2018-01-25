/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

/**
 * Unchecked exception for re-throwing cryptographic exceptions which are caused by configuration
 * errors. Exceptions of this type should never be raised in production code.
 * 
 * @author Max Spencer <ms955@cam.ac.uk>
 * 
 */
public class CryptoRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -4102778298152030517L;

    public CryptoRuntimeException() {
        super();
    }

    public CryptoRuntimeException(
            final String message, final Throwable cause) {
        super(message, cause);
    }

    public CryptoRuntimeException(final String message) {
        super(message);
    }

    public CryptoRuntimeException(final Throwable cause) {
        super(cause);
    }
}
