package org.cipollino.core.test;

import javassist.ClassPool;

import org.cipollino.core.DIModule;
import org.cipollino.core.inst.ClassTransformer;
import org.cipollino.core.model.MethodDef;
import org.cipollino.core.model.TargetDef;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

@Test
public class TransformationTest {

	@Inject
	ClassTransformer transformer;

	@Inject
	org.cipollino.core.runtime.Runtime runtime;

	@BeforeClass
	void init() {
		Injector injector = Guice.createInjector(new DIModule());
		injector.injectMembers(this);
	}

	@Test
	void addInterceptorTest() throws Exception {
		MethodDef method = new MethodDef();
		method.setName("String org.cipollino.core.test.data.Transformation1.concat(String s1, String s2)");
		method.getArguments().put("s1", "java.lang.String");
		method.getArguments().put("s2", "java.lang.String");

		TargetDef targetDef = new TargetDef();
		targetDef.getMethods().put(method.getName(), method);

		ClassPool cp = ClassPool.getDefault();
		// CtClass ctClass = cp.get(method.getClassName());
		// transformer.addInterceptor(interceptor, ctClass);
		//
		// Class clazz = ctClass.toClass();
		// Object instance = clazz.newInstance();
		// Object r1 = clazz.getMethod(method.getMethodName(), new Class[] {
		// String.class, String.class }).invoke(
		// instance, new Object[] { "a", "b" });
		//
	}
}
