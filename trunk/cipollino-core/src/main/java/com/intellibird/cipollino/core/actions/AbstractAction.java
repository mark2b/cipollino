package com.intellibird.cipollino.core.actions;

import com.intellibird.cipollino.core.model.ActionDef;
import com.intellibird.cipollino.core.model.ScriptDef;
import com.intellibird.cipollino.core.runtime.CallState;
import com.intellibird.cipollino.core.runtime.Runtime;
import com.intellibird.cipollino.core.runtime.Script;

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
