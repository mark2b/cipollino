package com.intellibird.cipollino.core.runtime;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.google.inject.Singleton;
import com.intellibird.cipollino.core.actions.ActionsRunner;
import com.intellibird.cipollino.core.model.MethodDef;

@Singleton
public class Runtime {

	private static Runtime instance;

	private Map<String, ClassData> classesMap = new LinkedHashMap<String, ClassData>();

	private Map<String, byte[]> originClassesMap = new LinkedHashMap<String, byte[]>();

	private ThreadLocal<Stack<ActionsRunner>> currentRunner = new ThreadLocal<Stack<ActionsRunner>>();

	/**
	 * Class
	 */
	private Map<String, List<MethodDef>> methodsMap = new LinkedHashMap<String, List<MethodDef>>();

	private Map<Integer, Object> elementsMap = new HashMap<Integer, Object>();

	private Map<String, Class<Script>> implClassMap = new LinkedHashMap<String, Class<Script>>();

	public Runtime() {
		instance = this;
	}

	private synchronized void addElement(Object element) {
		elementsMap.put(element.hashCode(), element);
	}

	@SuppressWarnings("unchecked")
	public <T> T getElement(Class<T> clazz, int key) {
		return (T) elementsMap.get(key);
	}

	public Object getElement(int key) {
		return elementsMap.get(key);
	}

	public boolean needTransformation(String className) {
		return methodsMap.containsKey(className);
	}

	public List<MethodDef> getMethods(String className) {
		return methodsMap.get(className);
	}

	public static Runtime getInstance() {
		return instance;
	}

	public void saveOriginClass(String className, byte[] bytecode) {
		if (!originClassesMap.containsKey(className)) {
			originClassesMap.put(className, bytecode);
		}
	}

	public byte[] getOriginClass(String className) {
		return originClassesMap.get(className);
	}

	public void registerMethod(MethodDef methodDef) {
		addElement(methodDef);
		List<MethodDef> methods = methodsMap.get(methodDef.getClassName());
		if (methods == null) {
			methods = new LinkedList<MethodDef>();
			methodsMap.put(methodDef.getClassName(), methods);
		}
		methods.add(methodDef);
		if (!classesMap.containsKey(methodDef.getClassName())) {
			classesMap.put(methodDef.getClassName(), new ClassData());
		}
	}

	public Set<String> getTransformedClasses() {
		return classesMap.keySet();
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

	public void reset() {
		classesMap.clear();
		originClassesMap.clear();
		methodsMap.clear();
		elementsMap.clear();
		implClassMap.clear();
	}
}
