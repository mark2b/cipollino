package org.cipollino.core.model;

public class ScriptDef {

	private String sourceCode;

	private String assignTo;

	private String implClassName;

	private String globalContext;

	private String classContext;

	private String instanceContext;

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getAssignTo() {
		return assignTo;
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	public String getImplClassName() {
		return implClassName;
	}

	public void setImplClassName(String implClassName) {
		this.implClassName = implClassName;
	}

	public String getGlobalContext() {
		return globalContext;
	}

	public void setGlobalContext(String globalContext) {
		this.globalContext = globalContext;
	}

	public String getClassContext() {
		return classContext;
	}

	public void setClassContext(String classContext) {
		this.classContext = classContext;
	}

	public String getInstanceContext() {
		return instanceContext;
	}

	public void setInstanceContext(String instanceContext) {
		this.instanceContext = instanceContext;
	}
}
