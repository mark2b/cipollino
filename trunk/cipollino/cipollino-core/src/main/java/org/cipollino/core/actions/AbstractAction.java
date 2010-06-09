package org.cipollino.core.actions;

import static org.cipollino.core.error.ErrorCode.ActionExecutionFailed;

import org.cipollino.core.model.ActionDef;
import org.cipollino.core.model.ScriptDef;
import org.cipollino.core.runtime.CallContext;
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

	protected void executeScripts(CallContext callContext, String phase) {
		for (ScriptDef scriptDef : getActionDef().getScriptDef()) {
			try {
				Class<Script> clazz = runtime.getImplClassMap().get(scriptDef.getImplClassName());
				Script script = clazz.newInstance();
				Object o = script.invoke(callContext);
				if (scriptDef.getAssignTo() != null && o != null) {
					callContext.getStateMap().put(scriptDef.getAssignTo(), o);
				}
			} catch (Exception e) {
				ActionExecutionFailed.print(phase, callContext.getMethodDef().getMethodName(), e);
			}
		}
	}

	@Override
	public void executeBefore(CallContext callContext) {
		executeScripts(callContext, "before");
		onExecuteBefore(callContext);
	}

	@Override
	public void executeAfter(CallContext callContext) {
		executeScripts(callContext, "after");
		onExecuteAfter(callContext);
	}

	@Override
	public void executeOnException(CallContext callContext) {
		executeScripts(callContext, "exception");
		onException(callContext);
	}

	abstract protected boolean onExecuteBefore(CallContext callContext);

	abstract protected boolean onExecuteAfter(CallContext callContext);

	abstract protected boolean onException(CallContext callContext);
}
