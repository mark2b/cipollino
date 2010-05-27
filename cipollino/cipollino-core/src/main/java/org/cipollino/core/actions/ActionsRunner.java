package org.cipollino.core.actions;

import static org.cipollino.core.error.ErrorCode.ActionExecutionFailed;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.cipollino.core.model.ActionDef;
import org.cipollino.core.model.MethodDef;
import org.cipollino.core.runtime.CallState;

public class ActionsRunner {

	private final List<Action> actions = new LinkedList<Action>();

	private final List<Action> reverseActions;

	private final CallState callState = new CallState();

	public ActionsRunner(Object target, MethodDef methodDef, Object[] parameters, List<ActionDef> actionsDef) {

		for (ActionDef actionDef : actionsDef) {
			AbstractAction action = (AbstractAction) actionDef.createAction();
			action.setActionDef(actionDef);
			actions.add(action);
		}
		reverseActions = new LinkedList<Action>(actions);
		Collections.reverse(reverseActions);

		callState.setTarget(target);
		callState.setMethodDef(methodDef);
		callState.setParameters(parameters);

	}

	public boolean beforeMethod() {
		for (Action action : actions) {
			try {
				action.executeBefore(callState);
			} catch (Exception e) {
				ActionExecutionFailed.print("before", callState.getMethodDef().getMethodName(), e);
				return false;
			}
		}
		return true;
	}

	public boolean afterMethod() {
		for (Action action : actions) {
			try {
				action.executeAfter(callState);
			} catch (Exception e) {
				ActionExecutionFailed.print("after", callState.getMethodDef().getMethodName(), e);
				return false;
			}
		}
		return true;
	}

	public void onException() {
	}

	public void onFinally() {
	}

	public CallState getCallState() {
		return callState;
	}
}
