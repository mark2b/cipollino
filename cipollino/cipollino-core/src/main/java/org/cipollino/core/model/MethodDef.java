package org.cipollino.core.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class MethodDef {

	private TargetDef targetDef;

	private String className;

	private String methodName;

	private String name;

	private boolean staticMethod = false;

	private boolean deleted = false;

	final private String uuid;

	private Map<String, String> arguments = new LinkedHashMap<String, String>();

	public MethodDef() {
		this.uuid = UUID.randomUUID().toString();
	}

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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isStaticMethod() {
		return staticMethod;
	}

	public void setStaticMethod(boolean staticMethod) {
		this.staticMethod = staticMethod;
	}

	public String getUuid() {
		return uuid;
	}
}
