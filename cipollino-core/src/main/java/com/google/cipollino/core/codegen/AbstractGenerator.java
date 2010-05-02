package com.google.cipollino.core.codegen;

import com.google.cipollino.core.model.MethodDef;

public class AbstractGenerator {

	private MethodDef methodDef;

	public MethodDef getMethodDef() {
		return methodDef;
	}

	public void setMethodDef(MethodDef methodDef) {
		this.methodDef = methodDef;
	}
}
