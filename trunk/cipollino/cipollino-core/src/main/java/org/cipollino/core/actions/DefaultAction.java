package org.cipollino.core.actions;

import org.cipollino.core.runtime.CallState;

public class DefaultAction extends AbstractAction {

	@Override
	protected boolean onExecuteBefore(CallState callState) {
		return true;
	}

	@Override
	protected boolean onExecuteAfter(CallState callState) {
		return true;
	}

	@Override
	protected boolean onException(CallState callState) {
		return true;
	}

}
