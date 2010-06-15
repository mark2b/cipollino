package org.cipollino.core.runtime;

public interface Script {

	Object invoke(CallContext callContext);
}
