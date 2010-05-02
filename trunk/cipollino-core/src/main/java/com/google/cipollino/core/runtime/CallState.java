package com.google.cipollino.core.runtime;

import java.util.HashMap;
import java.util.Map;

import com.google.cipollino.core.model.MethodDef;

public class CallState {

	final public static String TARGET = "$target";

	final public static String METHOD_DEF = "$methodDef";

	final public static String PARAMETERS_TYPES = "$parametersTypes";

	final public static String PARAMETERS = "$parameters";

	final public static String RESULT = "$result";

	final public static String EXCEPTION = "$exception";

	private Map<Object, Object> stateMap = new HashMap<Object, Object>();

	public Map<Object, Object> getStateMap() {
		return stateMap;
	}

	public MethodDef getMethodDef() {
		return (MethodDef) stateMap.get(METHOD_DEF);
	}

	public void setMethodDef(MethodDef methodDef) {
		stateMap.put(METHOD_DEF, methodDef);
	}

	@SuppressWarnings("unchecked")
	public Class[] getParametersTypes() {
		return (Class[]) stateMap.get(PARAMETERS_TYPES);
	}

	@SuppressWarnings("unchecked")
	public void setParametersTypes(Class[] parametersTypes) {
		stateMap.put(PARAMETERS_TYPES, parametersTypes);
	}

	public Object[] getParameters() {
		return (Object[]) stateMap.get(PARAMETERS);
	}

	public void setParameters(Object[] parameters) {
		stateMap.put(PARAMETERS, parameters);
	}

	public Object getResult() {
		return stateMap.get(RESULT);
	}

	public void setResult(Object result) {
		stateMap.put(RESULT, result);
	}

	public Exception getException() {
		return (Exception) stateMap.get(EXCEPTION);
	}

	public void setException(Throwable e) {
		stateMap.put(EXCEPTION, e);
	}

	public Object getTarget() {
		return stateMap.get(TARGET);
	}

	public void setTarget(Object target) {
		stateMap.put(TARGET, target);
	}

	public boolean isSuccess() {
		return getException() == null;
	}

	public boolean isFailed() {
		return getException() != null;
	}
}
