package org.cipollino.core.runtime;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import org.cipollino.core.actions.ActionsRunner;
import org.cipollino.core.model.MethodDef;

import com.google.inject.Singleton;

@Singleton
public class Runtime {

	private static Runtime instance;

	private Map<String, ClassData> classesMap = new LinkedHashMap<String, ClassData>();

	private ThreadLocal<Stack<ActionsRunner>> currentRunner = new ThreadLocal<Stack<ActionsRunner>>();

	private Map<String, Object> elementsMap = new HashMap<String, Object>();

	private Map<String, Class<Script>> implClassMap = new LinkedHashMap<String, Class<Script>>();

	public Runtime() {
		instance = this;
	}

	@SuppressWarnings("unchecked")
	public <T> T getElement(Class<T> clazz, String key) {
		return (T) elementsMap.get(key);
	}

	public Object getElement(String key) {
		return elementsMap.get(key);
	}

	public boolean needTransformation(String className) {
		return classesMap.containsKey(className);
	}

	public static Runtime getInstance() {
		return instance;
	}

	public void registerClass(String className, ClassData classData) {
		// if (classesMap.containsKey(className)) {
		// throw new IllegalStateException("The class already registered - "
		// + className);
		// }
		classesMap.put(className, classData);
		for (MethodDef methodDef : classData.getMethods()) {
			System.out.println(methodDef.getUuid() + " " + methodDef);
			elementsMap.put(methodDef.getUuid(), methodDef);
		}
	}

	public void cleanDeletedItems() {
		String[] keys = classesMap.keySet().toArray(
				new String[classesMap.size()]);
		for (String key : keys) {
			if (classesMap.get(key).getState().equals(ClassState.DELETED)) {
				classesMap.remove(key);
			}
		}
		// Integer[] hashCodes = elementsMap.keySet().toArray(
		// new Integer[elementsMap.size()]);
		// for (Integer hashCode : hashCodes) {
		// if (elementsMap.get(hashCode) instanceof MethodDef) {
		// MethodDef method = (MethodDef) elementsMap.get(hashCode);
		// if (method.isDeleted()) {
		// elementsMap.remove(method.hashCode());
		// }
		// }
		// }
	}

	public ClassData getClassData(String className) {
		return classesMap.get(className);
	}

	public void pushRunner(ActionsRunner runner) {
		Stack<ActionsRunner> stack = currentRunner.get();
		if (stack == null) {
			stack = new Stack<ActionsRunner>();
			currentRunner.set(stack);
		}
		stack.push(runner);
	}

	public ActionsRunner peekRunner() {
		Stack<ActionsRunner> stack = currentRunner.get();
		if (stack != null) {
			return stack.peek();
		} else {
			throw new IllegalStateException();
		}
	}

	public ActionsRunner popRunner() {
		Stack<ActionsRunner> stack = currentRunner.get();
		if (stack != null) {
			return stack.pop();
		} else {
			throw new IllegalStateException();
		}
	}

	public void registerImplClass(String className, Class<Script> clazz) {
		implClassMap.put(className, clazz);
	}

	public Map<String, Class<Script>> getImplClassMap() {
		return implClassMap;
	}

	public Set<String> getTargetClasses() {
		return classesMap.keySet();
	}
}
