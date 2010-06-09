package org.cipollino.core.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserContext {

	private final Map<Object, Object> globalContext = Collections.synchronizedMap(new HashMap<Object, Object>(10));

	private final Map<Object, Map<Object, Object>> classContexts = new HashMap<Object, Map<Object, Object>>(10);

	private final Map<Object, Map<Object, Object>> instanceContexts = new HashMap<Object, Map<Object, Object>>(10);

	public Map<Object, Object> getGlobalContext() {
		return globalContext;
	}

	synchronized public Map<Object, Object> getClassContext(String className) {
		Map<Object, Object> classContext = classContexts.get(className);
		if (classContext == null) {
			classContext = Collections.synchronizedMap(new HashMap<Object, Object>(10));
			classContexts.put(className, classContext);
		}
		return classContext;
	}

	public Map<Object, Object> getInstanceContext(Object instance) {
		Map<Object, Object> instanceContext = instanceContexts.get(instance);
		if (instanceContext == null) {
			instanceContext = Collections.synchronizedMap(new HashMap<Object, Object>(10));
			instanceContexts.put(instance, instanceContext);
		}
		return instanceContext;
	}
}
