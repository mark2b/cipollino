package org.cipollino.core.actions;

import org.cipollino.core.runtime.CallContext;

public interface Action {

	void executeBefore(CallContext callState);

	void executeAfter(CallContext callState);

	void executeOnException(CallContext callState);
}
