package org.cipollino.core.runtime;

public interface Script {

	Object invoke(CallState callState);
}
