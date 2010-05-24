package org.cipollino.core.runtime;

public class ClassData {

	private byte[] originByteCode = null;

	private Class<?> redefinedClass;

	private ClassLoader classLoader;

	public byte[] getOriginByteCode() {
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
}
