package com.google.cipollino.core.runtime;

public interface Script {

	Object invoke(CallState callState);
}
