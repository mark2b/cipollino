package com.google.cipollino.core.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class MethodDef {

	private DirectiveDef directiveDef;

	private String returnType;

	private String className;

	private String methodName;

	private String name;

	private Map<String, String> arguments = new LinkedHashMap<String, String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Map<String, String> getArguments() {
		return arguments;
	}

	public DirectiveDef getDirectiveDef() {
		return directiveDef;
	}

	public void setDirectiveDef(DirectiveDef directiveDef) {
		this.directiveDef = directiveDef;
	}

	public String createHiddenName() {
		return String.format("___%s$hidden", getMethodName());
	}
}
