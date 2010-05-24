package org.cipollino.logger.actions;

import org.cipollino.core.runtime.CallState;

public interface Formatter {

	String format(String format, CallState callState);
}
