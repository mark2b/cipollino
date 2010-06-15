package org.cipollino.core.actions;

import org.cipollino.core.runtime.CallContext;

public interface Action {

	void executeBefore(CallContext callContext);

	void executeAfter(CallContext callContext);

	void executeOnException(CallContext callContext);
}
