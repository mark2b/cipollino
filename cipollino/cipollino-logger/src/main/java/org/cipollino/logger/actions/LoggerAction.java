package org.cipollino.logger.actions;

import org.cipollino.core.DI;
import org.cipollino.core.actions.AbstractAction;
import org.cipollino.core.runtime.CallContext;
import org.cipollino.logger.model.FormatDef;
import org.cipollino.logger.model.LoggerActionDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggerAction extends AbstractAction {

	private Logger logger = LoggerFactory.getLogger(LoggerAction.class);

	private Formatter beforeFormatter;

	private Formatter afterFormatter;

	private Formatter exceptionFormatter;

	@Override
	public boolean onExecuteBefore(CallContext callState) {
		FormatDef formatDef = getLoggerActionDef().getBeforeFormatDef();
		String format = formatDef.getFormat();
		Formatter formatter = getBeforeFormatter();
		String output = formatter.format(format, callState);
		logger.info(output);
		return true;
	}

	@Override
	public boolean onExecuteAfter(CallContext callState) {
		FormatDef formatDef = getLoggerActionDef().getAfterFormatDef();

		String format = formatDef.getFormat();

		Formatter formatter = getAfterFormatter();
		String output = formatter.format(format, callState);
		logger.info(output);
		return true;
	}

	@Override
	public boolean onException(CallContext callState) {
		FormatDef formatDef = getLoggerActionDef().getExceptionFormatDef();

		String format = formatDef.getFormat();

		Formatter formatter = getExceptionFormatter();
		String output = formatter.format(format, callState);
		logger.info(output);
		return true;
	}

	private Formatter getBeforeFormatter() {
		if (beforeFormatter == null) {
			beforeFormatter = createFormatter();
		}
		return beforeFormatter;
	}

	private Formatter getAfterFormatter() {
		if (afterFormatter == null) {
			afterFormatter = createFormatter();
		}
		return afterFormatter;
	}

	private Formatter getExceptionFormatter() {
		if (exceptionFormatter == null) {
			exceptionFormatter = createFormatter();
		}
		return exceptionFormatter;
	}

	private Formatter createFormatter() {
		Formatter formatter = new SimpleFormatter(getLoggerActionDef()
				.getStyleDef());
		DI.getRootInjector().injectMembers(formatter);
		return formatter;
	}

	public LoggerActionDef getLoggerActionDef() {
		return (LoggerActionDef) getActionDef();
	}
}
