package org.cipollino.core.model;

import java.util.ArrayList;
import java.util.List;

import org.cipollino.core.actions.Action;
import org.cipollino.core.actions.DefaultAction;

public class ActionDef {

	private List<ScriptDef> scriptDef = new ArrayList<ScriptDef>();

	private boolean globalContext = false;

	private boolean classContext = false;

	private boolean instanceContext = false;

	public Action createAction() {
		return new DefaultAction();
	}

	public List<ScriptDef> getScriptDef() {
		return scriptDef;
	}

	public boolean isGlobalContext() {
		return globalContext;
	}

	public void setGlobalContext(boolean globalContext) {
		this.globalContext = globalContext;
	}

	public boolean isClassContext() {
		return classContext;
	}

	public void setClassContext(boolean classContext) {
		this.classContext = classContext;
	}

	public boolean isInstanceContext() {
		return instanceContext;
	}

	public void setInstanceContext(boolean instanceContext) {
		this.instanceContext = instanceContext;
	}

}
