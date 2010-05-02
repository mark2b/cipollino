package com.intellibird.cipollino.agent.error;

import java.util.Locale;

import com.intellibird.cipollino.core.annotations.Message;
import com.intellibird.cipollino.core.error.ErrorClass;
import com.intellibird.cipollino.core.i18n.MessageFormatter;

public enum ErrorCode {
	/**
	 * 
	 */
	@Message(id = "00601", value = "Cipollino Agent started.")
	AgentStartedMessage,
	/**
	 * 
	 */
	@Message(id = "00602", value = "Cipollino Agent stopped.")
	AgentStoppedMessage,
	/**
	 * 
	 */
	@Message(id = "00603", value = "Agent starting failed, [%s]")
	AgentStartingError;

	ErrorCode() {
		this(ErrorClass.Error);
	}

	ErrorCode(ErrorClass errorClass) {
		this.errorClass = errorClass;
	}

	private final static MessageFormatter FORMATTER = new MessageFormatter(ErrorCode.class, Locale.getDefault());

	private final ErrorClass errorClass;

	public ErrorClass getErrorClass() {
		return errorClass;
	}

	public String formatMessage(Object... params) {
		return FORMATTER.format(this, params);
	}
}
