package com.intellibird.cipollino.logger.actions;

import com.intellibird.cipollino.core.runtime.CallState;

public interface Formatter {

	String format(String format, CallState callState);
}
