/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.visualcode;

/**
 * Invalid visual code exception.
 * 
 * @author Graeme Jenkinson <gcj21@cam.ac.uk>
 * 
 */
public class InvalidVisualCodeException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidVisualCodeException() {
        super();
    }

    public InvalidVisualCodeException(final String message) {
        super(message);
    }

	public InvalidVisualCodeException(Throwable cause) {
		super(cause);
	}

	public InvalidVisualCodeException(String message, Throwable cause) {
		super(message, cause);
	}
}
