package com.google.cipollino.core.actions;

import com.google.cipollino.core.model.ActionDef;
import com.google.cipollino.core.model.ScriptDef;
import com.google.cipollino.core.runtime.CallState;
import com.google.cipollino.core.runtime.Runtime;
import com.google.cipollino.core.runtime.Script;

abstract public class AbstractAction implements Action {

	private ActionDef actionDef;

	private Runtime runtime = Runtime.getInstance();

	public ActionDef getActionDef() {
		return actionDef;
	}

	public void setActionDef(ActionDef actionDef) {
		this.actionDef = actionDef;
	}

	protected void executeScripts(CallState callState, boolean before) {
		for (ScriptDef scriptDef : getActionDef().getScriptDef()) {
			try {
				Class<Script> clazz = runtime.getImplClassMap().get(scriptDef.getImplClassName());
				Script script = clazz.newInstance();
				Object o = script.invoke(callState);
				if (scriptDef.getAssignTo() != null && o != null) {
					callState.getStateMap().put(scriptDef.getAssignTo(), o);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
