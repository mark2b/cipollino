package org.cipollino.core.error;

public class ErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	final private ErrorMessage errorMessage;
	final private Object[] args;

	public ErrorException(ErrorMessage errorMessage, Object... args) {
		super(errorMessage.format(args));
		this.errorMessage = errorMessage;
		this.args = args;
	}

	public ErrorException(ErrorMessage errorMessage, Throwable cause,
			Object... args) {
		super(errorMessage.format(args), cause);
		this.errorMessage = errorMessage;
		this.args = args;
	}

	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}

	public Object[] getArgs() {
		return args;
	}
}
