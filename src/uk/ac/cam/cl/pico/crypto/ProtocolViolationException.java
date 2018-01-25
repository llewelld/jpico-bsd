/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

public class ProtocolViolationException extends Exception {

	private static final long serialVersionUID = 3922332039175403488L;

	public ProtocolViolationException() {
		super();
	}

	public ProtocolViolationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolViolationException(String message) {
		super(message);
	}

	public ProtocolViolationException(Throwable cause) {
		super(cause);
	}
}