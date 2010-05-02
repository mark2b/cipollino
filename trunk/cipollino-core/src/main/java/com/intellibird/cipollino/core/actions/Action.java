package com.intellibird.cipollino.core.actions;

import com.intellibird.cipollino.core.runtime.CallState;

public interface Action {

	void executeBefore(CallState callState);

	void executeAfter(CallState callState);
}
