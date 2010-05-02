package com.google.cipollino.logger.actions;

import com.google.cipollino.core.runtime.CallState;

public interface Formatter {

	String format(String format, CallState callState);
}
