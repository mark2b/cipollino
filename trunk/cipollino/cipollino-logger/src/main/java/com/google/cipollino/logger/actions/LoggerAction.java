package com.google.cipollino.logger.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cipollino.core.actions.AbstractAction;
import com.google.cipollino.core.runtime.CallState;
import com.google.cipollino.logger.model.FormatDef;
import com.google.cipollino.logger.model.LoggerActionDef;

public class LoggerAction extends AbstractAction {

	private Logger logger = LoggerFactory.getLogger(LoggerAction.class);

	private Formatter beforeFormatter;

	private Formatter afterFormatter;

	@Override
	public void executeBefore(CallState callState) {
		executeScripts(callState, true);
		FormatDef formatDef = getLoggerActionDef().getBeforeFormatDef();
		String format = formatDef.getFormat();
		Formatter formatter = getBeforeFormatter();
		String output = formatter.format(format, callState);
		logger.info(output);
	}

	@Override
	public void executeAfter(CallState callState) {
		executeScripts(callState, false);
		FormatDef formatDef = getLoggerActionDef().getAfterFormatDef();

		String format = formatDef.getFormat();

		Formatter formatter = getAfterFormatter();
		String output = formatter.format(format, callState);
		logger.info(output);
	}

	private Formatter getBeforeFormatter() {
		if (beforeFormatter == null) {
			beforeFormatter = getFormatter(getLoggerActionDef().getBeforeFormatDef().getStyle());
		}
		return beforeFormatter;
	}

	private Formatter getAfterFormatter() {
		if (afterFormatter == null) {
			afterFormatter = getFormatter(getLoggerActionDef().getBeforeFormatDef().getStyle());
		}
		return afterFormatter;
	}

	private Formatter getFormatter(String style) {
		return style.equals("json") ? new JSONFormatter() : new SimpleFormatter();
	}

	public LoggerActionDef getLoggerActionDef() {
		return (LoggerActionDef) getActionDef();
	}
}
