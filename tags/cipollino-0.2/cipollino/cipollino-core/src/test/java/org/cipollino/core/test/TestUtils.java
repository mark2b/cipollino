package org.cipollino.core.test;

import java.lang.reflect.Method;

import com.google.inject.Singleton;

@Singleton
public class TestUtils {

	@SuppressWarnings("unchecked")
	public Method getPrivateMethod(Object instance, String methodName,
			Class... classes) throws NoSuchMethodException {
		Method method = instance.getClass().getDeclaredMethod(methodName,
				classes);
		method.setAccessible(true);
		return method;
	}
}
