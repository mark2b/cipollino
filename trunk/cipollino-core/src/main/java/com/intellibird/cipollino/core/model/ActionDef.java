package com.intellibird.cipollino.core.model;

import java.util.ArrayList;
import java.util.List;

import com.intellibird.cipollino.core.actions.Action;

public abstract class ActionDef {

	private List<ScriptDef> scriptDef = new ArrayList<ScriptDef>();

	abstract public Action createAction();

	public List<ScriptDef> getScriptDef() {
		return scriptDef;
	}

}
