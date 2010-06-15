package org.cipollino.logger.actions;

import org.cipollino.core.runtime.CallContext;

public interface Formatter {

	String format(String format, CallContext callState);
}
