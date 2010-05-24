package org.cipollino.core.actions;

import org.cipollino.core.runtime.CallState;

public interface Action {

	void executeBefore(CallState callState);

	void executeAfter(CallState callState);

	void executeOnException(CallState callState);
}
