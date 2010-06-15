package org.cipollino.core.runtime;

import java.util.HashMap;
import java.util.Map;

import org.cipollino.core.model.MethodDef;

public class CallContext {

	final public static String TARGET = "$target";

	final public static String METHOD_DEF = "$methodDef";

	final public static String PARAMETERS = "$parameters";

	final public static String RESULT = "$result";

	final public static String EXCEPTION = "$exception";

	private UserContext userContext = null;

	private Map<Object, Object> contextMap = new HashMap<Object, Object>();

	public Map<Object, Object> getContext() {
		return contextMap;
	}

	public MethodDef getMethodDef() {
		return (MethodDef) contextMap.get(METHOD_DEF);
	}

	public void setMethodDef(MethodDef methodDef) {
		contextMap.put(METHOD_DEF, methodDef);
	}

	public Object[] getParameters() {
		return (Object[]) contextMap.get(PARAMETERS);
	}

	public void setParameters(Object[] parameters) {
		contextMap.put(PARAMETERS, parameters);
	}

	public Object getResult() {
		return contextMap.get(RESULT);
	}

	public void setResult(Object result) {
		contextMap.put(RESULT, result);
	}

	public void setResult(int result) {
		contextMap.put(RESULT, result);
	}

	public void setResult(boolean result) {
		contextMap.put(RESULT, result);
	}

	public void setResult(double result) {
		contextMap.put(RESULT, result);
	}

	public Exception getException() {
		return (Exception) contextMap.get(EXCEPTION);
	}

	public void setException(Throwable e) {
		contextMap.put(EXCEPTION, e);
	}

	public Object getTarget() {
		return contextMap.get(TARGET);
	}

	public void setTarget(Object target) {
		contextMap.put(TARGET, target);
	}

	public boolean isSuccess() {
		return getException() == null;
	}

	public boolean isFailed() {
		return getException() != null;
	}

	public UserContext getUserContext() {
		return userContext;
	}

	public void setUserContext(UserContext userContext) {
		this.userContext = userContext;
	}

	public Map<Object, Object> getGlobalContext() {
		return userContext.getGlobalContext();
	}

	public Map<Object, Object> getClassContext() {
		return userContext.getClassContext(getMethodDef().getClassName());
	}

	public Map<Object, Object> getInstanceContext() {
		return userContext.getInstanceContext(getTarget());
	}

	public void destroy() {
		userContext.destroyInstanceContext(getTarget());
	}
}
