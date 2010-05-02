package com.intellibird.cipollino.core.test;

import javassist.ClassPool;
import javassist.CtClass;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.intellibird.cipollino.core.DIModule;
import com.intellibird.cipollino.core.inst.ClassTransformer;
import com.intellibird.cipollino.core.model.Directives;
import com.intellibird.cipollino.core.model.DirectiveDef;
import com.intellibird.cipollino.core.model.MethodDef;

@Test
public class TransformationTest {

	@Inject
	ClassTransformer transformer;

	@Inject
	com.intellibird.cipollino.core.runtime.Runtime runtime;

	@BeforeClass
	void init() {
		Injector injector = Guice.createInjector(new DIModule());
		injector.injectMembers(this);
	}

	@Test
	void addInterceptorTest() throws Exception {
		MethodDef method = new MethodDef();
		method.setName("String com.intellibird.cipollino.core.test.data.Transformation1.concat(String s1, String s2)");
		method.getArguments().put("s1", "java.lang.String");
		method.getArguments().put("s2", "java.lang.String");

		DirectiveDef directiveDef = new DirectiveDef();
		directiveDef.getMethods().put(method.getName(), method);

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
		// System.out.println(r1);
	}
}
