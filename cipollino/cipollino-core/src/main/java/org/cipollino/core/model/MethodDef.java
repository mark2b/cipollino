package org.cipollino.core.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class MethodDef {

	private TargetDef targetDef;


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

	public TargetDef getTargetDef() {
		return targetDef;
	}

	public void setTargetDef(TargetDef targetDef) {
		this.targetDef = targetDef;
	}

	public String createHiddenName() {
		return String.format("___%s$hidden", getMethodName());
	}
}
