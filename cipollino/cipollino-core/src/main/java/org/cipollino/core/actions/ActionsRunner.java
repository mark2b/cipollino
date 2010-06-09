package org.cipollino.core.actions;

import static org.cipollino.core.error.ErrorCode.ActionExecutionFailed;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.cipollino.core.model.ActionDef;
import org.cipollino.core.model.MethodDef;
import org.cipollino.core.runtime.CallContext;
import org.cipollino.core.runtime.Runtime;

public class ActionsRunner {

	private final List<Action> actions = new LinkedList<Action>();

	private final List<Action> reverseActions;

	private final CallContext callContext = new CallContext();

	public ActionsRunner(Object target, MethodDef methodDef, Object[] parameters, List<ActionDef> actionsDef) {

		for (ActionDef actionDef : actionsDef) {
			AbstractAction action = (AbstractAction) actionDef.createAction();
			action.setActionDef(actionDef);
			actions.add(action);
		}
		reverseActions = new LinkedList<Action>(actions);
		Collections.reverse(reverseActions);

		callContext.setTarget(target);
		callContext.setMethodDef(methodDef);
		callContext.setParameters(parameters);
		callContext.setUserContext(Runtime.getInstance().getUserContext());

	}

	public boolean beforeMethod() {
		for (Action action : actions) {
			try {
				action.executeBefore(callContext);
			} catch (Exception e) {
				ActionExecutionFailed.print("before", callContext.getMethodDef().getMethodName(), e);
				return false;
			}
		}
		return true;
	}

	public boolean afterMethod() {
		for (Action action : actions) {
			try {
				action.executeAfter(callContext);
			} catch (Exception e) {
				ActionExecutionFailed.print("after", callContext.getMethodDef().getMethodName(), e);
				return false;
			}
		}
		return true;
	}

	public void onException() {
	}

	public void onFinally() {
	}

	public CallContext getCallState() {
		return callContext;
	}
}
