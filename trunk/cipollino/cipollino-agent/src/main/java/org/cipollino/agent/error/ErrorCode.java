package org.cipollino.agent.error;

import org.cipollino.core.error.ErrorMessage;
import org.cipollino.core.error.ErrorUtils;
import org.cipollino.core.logger.LogManager;
import org.cipollino.core.logger.Message;
import org.cipollino.core.logger.Messages;
import org.cipollino.core.logger.Severity;

@Messages(bundleName = "cipollino-agent", scope = "Cipollino-Agent")
public enum ErrorCode implements ErrorMessage {
	/**
	 * 
	 */
	@Message(code = "00200", message = "Agent was started.", severity = Severity.INFO, suppressMessageCode = true)
	AgentWasStarted,
	/**
	 * 
	 */
	@Message(code = "00201", message = "The Control file is missing.")
	ControlFileMissing,
	/**
	 * 
	 */
	@Message(code = "00203", message = "The Control file [%s] not found.")
	ControlFileNotFound,

	/**
	 * 
	 */
	@Message(code = "00204", message = "Arguments Parsing failed.")
	ArgumentsParsingError,
	/**
	 * 
	 */
	@Message(code = "00205", message = "Agent was not started.")
	AgentWasnotStarted,
	/**
	 * 
	 */
	@Message(code = "00206", message = "The Process ID file [%s] not found.")
	PidFileNotFound,
	/**
	 * 
	 */
	@Message(code = "00207", message = "The Process ID missing.")
	PidMissing,
	/**
	 * 
	 */
	@Message(code = "00208", message = "The Process with ID [%s] not found.")
	ProcessNotFound,
	/**
	 * 
	 */
	@Message(code = "00209", message = "Connected.", severity = Severity.INFO, suppressMessageCode = true)
	AgentWasConnected,
	/**
	 * 
	 */
	@Message(code = "00210", message = "Connected to application failed. %s")
	AgentWasnotConnected;

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
