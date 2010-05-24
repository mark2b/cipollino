package org.cipollino.core.runtime;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.cipollino.core.actions.ActionsRunner;
import org.cipollino.core.error.ErrorCode;
import org.cipollino.core.model.MethodDef;

import com.google.inject.Singleton;

@Singleton
public class Runtime {

	private static Runtime instance;

	private Data data = new Data();

	public Runtime() {
		instance = this;
	}

	private synchronized void addElement(Object element) {
		data.elementsMap.put(element.hashCode(), element);
	}

	@SuppressWarnings("unchecked")
	public <T> T getElement(Class<T> clazz, int key) {
		return (T) data.elementsMap.get(key);
	}

	public Object getElement(int key) {
		return data.elementsMap.get(key);
	}

	public boolean needTransformation(String className) {
		return data.methodsMap.containsKey(className);
	}

	public List<MethodDef> getMethods(String className) {
		return data.methodsMap.get(className);
	}

	public static Runtime getInstance() {
		return instance;
	}

	public void saveOriginClass(String className, byte[] bytecode) {
		if (!data.originClassesMap.containsKey(className)) {
			data.originClassesMap.put(className, bytecode);
		}
	}

	public byte[] getOriginClass(String className) {
		return data.originClassesMap.get(className);
	}

	public void registerMethod(MethodDef methodDef) {
		ErrorCode.Trace.print("Register method " + methodDef.getMethodName());
		addElement(methodDef);
		List<MethodDef> methods = data.methodsMap.get(methodDef.getClassName());
		if (methods == null) {
			methods = new LinkedList<MethodDef>();
			data.methodsMap.put(methodDef.getClassName(), methods);
		}
		methods.add(methodDef);
		if (!data.classesMap.containsKey(methodDef.getClassName())) {
			data.classesMap.put(methodDef.getClassName(), new ClassData());
		}
	}

	public Set<String> getTransformedClasses() {
		return data.classesMap.keySet();
	}

	public ClassData getClassData(String className) {
		return data.classesMap.get(className);
	}

	public void pushRunner(ActionsRunner runner) {
		Stack<ActionsRunner> stack = data.currentRunner.get();
		if (stack == null) {
			stack = new Stack<ActionsRunner>();
			data.currentRunner.set(stack);
		}
		stack.push(runner);
	}

	public ActionsRunner peekRunner() {
		Stack<ActionsRunner> stack = data.currentRunner.get();
		if (stack != null) {
			return stack.peek();
		} else {
			throw new IllegalStateException();
		}
	}

	public ActionsRunner popRunner() {
		Stack<ActionsRunner> stack = data.currentRunner.get();
		if (stack != null) {
			return stack.pop();
		} else {
			throw new IllegalStateException();
		}
	}

	public void registerImplClass(String className, Class<Script> clazz) {
		data.implClassMap.put(className, clazz);
	}

	public Map<String, Class<Script>> getImplClassMap() {
		return data.implClassMap;
	}

	public void reset() {
		data.classesMap.clear();
		data.originClassesMap.clear();
		data.methodsMap.clear();
		data.elementsMap.clear();
		data.implClassMap.clear();
	}

	static class Data {

		private Map<String, ClassData> classesMap = new LinkedHashMap<String, ClassData>();

		private Map<String, byte[]> originClassesMap = new LinkedHashMap<String, byte[]>();

		private ThreadLocal<Stack<ActionsRunner>> currentRunner = new ThreadLocal<Stack<ActionsRunner>>();

		/**
		 * Class
		 */
		private Map<String, List<MethodDef>> methodsMap = new LinkedHashMap<String, List<MethodDef>>();

		private Map<Integer, Object> elementsMap = new HashMap<Integer, Object>();

		private Map<String, Class<Script>> implClassMap = new LinkedHashMap<String, Class<Script>>();
	}
}
