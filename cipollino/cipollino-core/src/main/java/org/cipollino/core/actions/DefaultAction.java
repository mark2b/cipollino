package org.cipollino.core.actions;

import org.cipollino.core.runtime.CallContext;

public class DefaultAction extends AbstractAction {

	@Override
	protected boolean onExecuteBefore(CallContext callState) {
		return true;
	}

	@Override
	protected boolean onExecuteAfter(CallContext callState) {
		return true;
	}

	@Override
	protected boolean onException(CallContext callState) {
		return true;
	}

}
