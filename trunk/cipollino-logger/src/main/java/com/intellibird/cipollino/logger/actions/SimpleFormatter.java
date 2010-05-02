package com.intellibird.cipollino.logger.actions;

public class SimpleFormatter extends AbstractFormatter {

	protected String formatParameter(Object object) {
		return object == null ? "null" : object.toString();
	}
}
