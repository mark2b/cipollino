package com.intellibird.cipollino.core.exception;

import com.intellibird.cipollino.core.error.ErrorCode;

public class ErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	final ErrorCode errorCode;

	final Object[] messageParams;

	public ErrorException(ErrorCode errorCode, Object... messageParams) {
		this(errorCode, null, messageParams);
	}

	public ErrorException(ErrorCode errorCode, Throwable cause, Object... messageParams) {
		super(errorCode.formatMessage(messageParams), cause);
		this.errorCode = errorCode;
		this.messageParams = messageParams;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public Object[] getMessageParams() {
		return messageParams;
	}
}
