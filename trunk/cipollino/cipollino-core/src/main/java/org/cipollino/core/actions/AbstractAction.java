package org.cipollino.core.actions;

import static org.cipollino.core.error.ErrorCode.ActionExecutionFailed;

import org.cipollino.core.model.ActionDef;
import org.cipollino.core.model.ScriptDef;
import org.cipollino.core.runtime.CallState;
import org.cipollino.core.runtime.Runtime;
import org.cipollino.core.runtime.Script;

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
				ActionExecutionFailed.print(before ? "before" : "after", callState.getMethodDef().getMethodName(), e);
			}
		}
	}

	@Override
	public void executeBefore(CallState callState) {
		executeScripts(callState, false);
		onExecuteBefore(callState);
	}

	@Override
	public void executeAfter(CallState callState) {
		executeScripts(callState, true);
		onExecuteAfter(callState);
	}

	@Override
	public void executeOnException(CallState callState) {
		executeScripts(callState, false);
		onException(callState);
	}

	abstract protected boolean onExecuteBefore(CallState callState);

	abstract protected boolean onExecuteAfter(CallState callState);

	abstract protected boolean onException(CallState callState);
}
