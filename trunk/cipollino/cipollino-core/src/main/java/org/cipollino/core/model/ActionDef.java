package org.cipollino.core.model;

import java.util.ArrayList;
import java.util.List;

import org.cipollino.core.actions.Action;
import org.cipollino.core.actions.DefaultAction;


public class ActionDef {

	private List<ScriptDef> scriptDef = new ArrayList<ScriptDef>();

	public Action createAction() {
		return new DefaultAction();
	}

	public List<ScriptDef> getScriptDef() {
		return scriptDef;
	}

}
