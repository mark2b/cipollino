package org.cipollino.core.runtime;

import java.util.ArrayList;
import java.util.List;

import org.cipollino.core.model.MethodDef;

public class ClassData {

	private final String className;

	private byte[] originByteCode = null;

	private Class<?> redefinedClass;

	private ClassLoader classLoader;

	private ClassState state;

	List<MethodDef> methods = new ArrayList<MethodDef>();

	public ClassData(String className) {
		this.className = className;
	}

	public byte[] getOriginBytecode() {
		return originByteCode;
	}

	public void setOriginByteCode(byte[] originByteCode) {
		this.originByteCode = originByteCode;
	}

	public boolean isLoaded() {
		return originByteCode != null;
	}

	public Class<?> getRedefinedClass() {
		return redefinedClass;
	}

	public void setRedefinedClass(Class<?> redefinedClass) {
		this.redefinedClass = redefinedClass;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ClassState getState() {
		return state;
	}

	public void setState(ClassState state) {
		this.state = state;
	}

	public List<MethodDef> getMethods() {
		return methods;
	}

	public String getClassName() {
		return className;
	}
}
