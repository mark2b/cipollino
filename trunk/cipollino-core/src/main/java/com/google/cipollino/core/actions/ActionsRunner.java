package com.google.cipollino.core.actions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.cipollino.core.model.ActionDef;
import com.google.cipollino.core.model.MethodDef;
import com.google.cipollino.core.runtime.CallState;

public class ActionsRunner {

	private final List<Action> actions = new LinkedList<Action>();

	private final List<Action> reverseActions;

	private final CallState callState = new CallState();

	public ActionsRunner(Object target, MethodDef methodDef, Class<?>[] parametersTypes, Object[] parameters,
			List<ActionDef> actionsDef) {

		for (ActionDef actionDef : actionsDef) {
			AbstractAction action = (AbstractAction) actionDef.createAction();
			action.setActionDef(actionDef);
			actions.add(action);
		}
		reverseActions = new LinkedList<Action>(actions);
		Collections.reverse(reverseActions);

		callState.setTarget(target);
		callState.setMethodDef(methodDef);
		callState.setParametersTypes(parametersTypes);
		callState.setParameters(parameters);

	}

	public void beforeMethod() {
		try {
			for (Action action : actions) {
				action.executeBefore(callState);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void afterMethod() {
		try {
			for (Action action : reverseActions) {
				action.executeAfter(callState);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onException() {
	}

	public void onFinally() {
	}

	public CallState getCallState() {
		return callState;
	}

}
