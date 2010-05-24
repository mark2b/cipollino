package org.cipollino.core.error;

import org.cipollino.core.logger.LogManager;
import org.cipollino.core.logger.Message;
import org.cipollino.core.logger.Messages;
import org.cipollino.core.logger.Severity;

@Messages(scope = "Cipollino-Core")
public enum ErrorCode implements ErrorMessage {
	/**
	 * 
	 */
	@Message(code = "00000", message = "Success.", severity = Severity.INFO)
	Success,
	/**
	 * 
	 */
	@Message(code = "00001", message = "Internal Error.")
	InternalError,
	/**
	 * 
	 */
	@Message(code = "00010", message = "%s", severity = Severity.INFO)
	Info,
	/**
	 * 
	 */
	@Message(code = "00011", message = "%s", severity = Severity.INFO)
	Trace,
	/**
	 * 
	 */
	@Message(code = "00012", message = "%s", severity = Severity.DEBUG)
	Debug,
	/**
	 * 
	 */
	@Message(code = "00101", message = "The Control file is missing.")
	ControlFileMissing,
	/**
	 * 
	 */
	@Message(code = "00102", message = "The Control file [%s] not found.")
	ControlFileNotFound,
	/**
	 * 
	 */
	@Message(code = "00103", message = "XML Parsing failed. [%s]")
	XmlParsingError,
	/**
	* 
	*/
	@Message(code = "00301", message = "Return type not found in the [%s].")
	ReturnTypeNotFound,
	/**
	 * 
	 */
	@Message(code = "00302", message = "Class name not found in the [%s].")
	ClassNameNotFound,
	/**
	 * 
	 */
	@Message(code = "00303", message = "Method name not found in the [%s].")
	MethodNameNotFound,
	/**
	 * 
	 */
	@Message(code = "00304", message = "Invalid method declaration in the [%s].")
	InvalidMethod,
	/**
	 * 
	 */
	@Message(code = "00501", message = "Action execution failed. Phase: %s, Method: %s.")
	ActionExecutionFailed(),
	/**
	 * 
	 */
	@Message(code = "00502", message = "Script execution failed. Phase: %s, Method: %s.")
	ScriptExecutionFailed(),
	/**
	 * 
	 */
	@Message(code = "00503", message = "Method not found [%s].")
	MethodNotFound,
	/**
	 * 
	 */
	@Message(code = "00504", message = "Class not found [%s].")
	ClassNotFound,
	/**
	 * 
	 */
	@Message(code = "00505", message = "Class not found [%s].")
	RuntimeClassNotFound,
	/**
	 * 
	 */
	@Message(code = "00506", message = "Compilation failure [%s].")
	CompilationFailure,
	/**
	 * 
	 */
	@Message(code = "00507", message = "Class can not be transformed [%s].")
	ClassCanNotBeTransformed,
	/**
	 * 
	 */
	@Message(code = "00508", message = "Class Path not found [%s].")
	ClassPathNotFound;

	private final static LogManager MANAGER = new LogManager(ErrorCode.class);

	public void print(Object... args) {
		MANAGER.print(this, null, args);
	}

	public String format(Object... args) {
		return MANAGER.format(this, args);
	}

	public String getCode() {
		return ErrorUtils.getCode(this);
	}
}
