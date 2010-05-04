package com.google.cipollino.core.actions;

import com.google.cipollino.core.runtime.CallState;

public interface Action {

	void executeBefore(CallState callState);

	void executeAfter(CallState callState);
}
