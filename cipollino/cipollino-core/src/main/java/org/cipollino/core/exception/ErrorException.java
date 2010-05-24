package org.cipollino.core.exception;

public class ErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ErrorException(String message) {
		super(message);
	}

	public ErrorException(String message, Throwable cause) {
		super(message, cause);
	}
}
