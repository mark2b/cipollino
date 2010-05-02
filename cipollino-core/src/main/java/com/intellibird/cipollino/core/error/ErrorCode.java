package com.intellibird.cipollino.core.error;

import java.lang.reflect.Field;
import java.util.Locale;

import com.intellibird.cipollino.core.annotations.Message;
import com.intellibird.cipollino.core.i18n.MessageFormatter;

public enum ErrorCode {
	/**
	 * 
	 */
	@Message(id = "00001", value = "Internal Error.")
	InternalError,
	/**
	 * 
	 */
	@Message(id = "00101", value = "The Control file is missing.")
	ControlFileMissing,
	/**
	 * 
	 */
	@Message(id = "00102", value = "The Control file [%s] not found.")
	ControlFileNotFound,
	/**
	 * 
	 */
	@Message(id = "00103", value = "XML Parsing failed. [%s]")
	XmlParsingError(ErrorClass.ValidationError),
	/**
	 * 
	 */
	@Message(id = "00104", value = "Arguments Parsing failed.")
	ArgumentsParsingError(ErrorClass.ValidationError),
	/**
	 * 
	 */
	@Message(id = "00105", value = "Agent was not started.")
	AgentWasnotStarted(ErrorClass.Info),
	/**
	* 
	*/
	@Message(id = "00301", value = "Return type not found in the [%s].")
	ReturnTypeNotFound(ErrorClass.ValidationError),
	/**
	 * 
	 */
	@Message(id = "00302", value = "Class name not found in the [%s].")
	ClassNameNotFound(ErrorClass.ValidationError),
	/**
	 * 
	 */
	@Message(id = "00303", value = "Method name not found in the [%s].")
	MethodNameNotFound(ErrorClass.ValidationError),
	/**
	 * 
	 */
	@Message(id = "00304", value = "Invalid method declaration in the [%s].")
	InvalidMethod(ErrorClass.ValidationError);

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

	public String getCode() {
		try {
			String name = (String) getClass().getMethod("name").invoke(this);
			Field field = getClass().getField(name);
			Message messageAnnotation = field.getAnnotation(Message.class);
			if (messageAnnotation != null) {
				String messageId = messageAnnotation.id();
				return messageId;
			}
			throw new IllegalStateException("Missing @Message annotation.");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
