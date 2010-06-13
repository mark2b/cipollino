package org.cipollino.core.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.cipollino.core.DIModule;
import org.cipollino.core.model.MethodDef;
import org.cipollino.core.model.ParameterDef;
import org.cipollino.core.services.ClassPathService;
import org.cipollino.core.services.TransformationService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

@Test
public class TransformationServiceTest {

	@Inject
	TestUtils testUtils;

	@Inject
	TransformationService transformationService;

	@Inject
	ClassPathService classPathService;

	@Inject
	org.cipollino.core.runtime.Runtime runtime;

	@BeforeClass
	void init() {
		Injector injector = Guice.createInjector(new DIModule());
		injector.injectMembers(this);
	}

	@Test
	void findCtMethodTest() throws NotFoundException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		MethodDef methodDef = new MethodDef();
		methodDef.setClassName("java.io.File");
		methodDef.setMethodName("list");

		CtClass fileClass = classPathService.getClassPool().get(
				methodDef.getClassName());

		CtMethod method = (CtMethod) testUtils.getPrivateMethod(
				transformationService, "findCtMethod", MethodDef.class,
				CtMethod[].class).invoke(transformationService, methodDef,
				fileClass.getDeclaredMethods());
		assertNotNull(method);

		methodDef = new MethodDef();
		methodDef.setClassName("java.io.File");
		methodDef.setMethodName("list");

		ParameterDef parameterDef = new ParameterDef();
		parameterDef.setType("java.io.FilenameFilter");
		methodDef.getParameters().put(0, parameterDef);

		method = (CtMethod) testUtils.getPrivateMethod(transformationService,
				"findCtMethod", MethodDef.class, CtMethod[].class).invoke(
				transformationService, methodDef,
				fileClass.getDeclaredMethods());
		assertNotNull(method);
	}

	@Test
	void updateMethodParametersTest() throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NotFoundException {
		MethodDef methodDef = new MethodDef();
		methodDef.setClassName("java.io.File");
		methodDef.setMethodName("list");

		CtClass fileClass = classPathService.getClassPool().get(
				methodDef.getClassName());

		CtMethod listMethod = fileClass.getMethod("list",
				"(Ljava/io/FilenameFilter;)[Ljava/lang/String;");
		testUtils.getPrivateMethod(transformationService,
				"updateMethodParameters", MethodDef.class, CtMethod.class)
				.invoke(transformationService, methodDef, listMethod);
		assertEquals(methodDef.getParameters().size(), 1);
		ParameterDef parameterDef = methodDef.getParameters().get(0);
		assertNotNull(parameterDef);
		assertEquals(parameterDef.getName(), "arg0");
		assertEquals(parameterDef.getType(), "java.io.FilenameFilter");

		methodDef = new MethodDef();
		methodDef.setClassName("java.io.File");
		methodDef.setMethodName("list");

		parameterDef = new ParameterDef();
		parameterDef.setName("p1");
		parameterDef.setType("t1");
		methodDef.getParameters().put(0, parameterDef);

		testUtils.getPrivateMethod(transformationService,
				"updateMethodParameters", MethodDef.class, CtMethod.class)
				.invoke(transformationService, methodDef, listMethod);
		assertEquals(methodDef.getParameters().size(), 1);
		parameterDef = methodDef.getParameters().get(0);
		assertNotNull(parameterDef);
		assertEquals(parameterDef.getName(), "p1");
		assertEquals(parameterDef.getType(), "java.io.FilenameFilter");
	}

}
