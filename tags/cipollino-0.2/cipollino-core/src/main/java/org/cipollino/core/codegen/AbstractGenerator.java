package org.cipollino.core.codegen;

import org.cipollino.core.model.ActionDef;
import org.cipollino.core.model.MethodDef;

public class AbstractGenerator {

	private ActionDef actionDef;

	private MethodDef methodDef;

	public MethodDef getMethodDef() {
		return methodDef;
	}

	public void setMethodDef(MethodDef methodDef) {
		this.methodDef = methodDef;
	}

	public ActionDef getActionDef() {
		return actionDef;
	}

	public void setActionDef(ActionDef actionDef) {
		this.actionDef = actionDef;
	}
}
