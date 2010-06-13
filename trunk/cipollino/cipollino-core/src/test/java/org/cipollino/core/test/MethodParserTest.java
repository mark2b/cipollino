package org.cipollino.core.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cipollino.core.DIModule;
import org.cipollino.core.model.MethodDef;
import org.cipollino.core.model.ParameterDef;
import org.cipollino.core.services.MethodParserService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class MethodParserTest {

	@Inject
	TestUtils testUtils;

	@Inject
	MethodParserService methodParserService;

	@BeforeClass
	void init() {
		Injector injector = Guice.createInjector(new DIModule());
		injector.injectMembers(this);
	}

	@Test
	void normalizeClassNameTest() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		String className = (String) testUtils.getPrivateMethod(
				methodParserService, "normalizeClassName", String.class)
				.invoke(methodParserService, "String");
		assertEquals(className, "java.lang.String");

		className = (String) testUtils.getPrivateMethod(methodParserService,
				"normalizeClassName", String.class).invoke(methodParserService,
				"java.io.File");
		assertEquals(className, "java.io.File");
	}

	@Test
	void parseMethodTest() {
		MethodDef methodDef = new MethodDef();
		methodDef.setName("java.io.File.list()");
		methodParserService.parseMethod(methodDef);
		assertEquals(methodDef.getParameters().size(), 0);

		ParameterDef parameterDef = new ParameterDef();
		parameterDef.setName("filter");
		methodDef.getParameters().put(0, parameterDef);
		methodParserService.parseMethod(methodDef);
		assertEquals(methodDef.getParameters().size(), 1);
		parameterDef = methodDef.getParameters().get(0);
		assertNull(parameterDef.getType());

		methodDef = new MethodDef();
		methodDef.setName("java.io.File.list(java.io.FilenameFilter filter)");
		methodParserService.parseMethod(methodDef);
		assertEquals(methodDef.getParameters().size(), 1);
		parameterDef = methodDef.getParameters().get(0);
		assertEquals(parameterDef.getType(), "java.io.FilenameFilter");
		assertEquals(parameterDef.getName(), "filter");
	}

	@Test(dataProvider = "classes", enabled = false)
	public void classPatternTest(String input, String expected) {
		Pattern pattern = Pattern.compile(MethodParserService.CLASS_PATTERN);
		Matcher matcher = pattern.matcher(input);
		boolean found = matcher.find();
		assertTrue(found);
		assertEquals(matcher.group(1), expected);
	}

	@Test(dataProvider = "methods", enabled = true)
	public void methodPatternTest(MethodDef method, String className,
			String methodName, String params) {
		methodParserService.parseMethod(method);
		assertEquals(method.getClassName(), className);
		assertEquals(method.getMethodName(), methodName);
		String[] paramsPairs = params.split("\\s*,\\s*");
		Iterator<Entry<Integer, ParameterDef>> parametersIt = method
				.getParameters().entrySet().iterator();
		for (String paramPair : paramsPairs) {
			Entry<Integer, ParameterDef> parameterDef = parametersIt.next();
			String[] tokens = paramPair.split("\\s+");
			assertEquals(parameterDef.getValue().getType(), tokens[0]);
			assertEquals(parameterDef.getValue().getName(), tokens[1]);
		}
	}

	@DataProvider(name = "classes")
	Object[][] getClasses() {
		return new String[][] {
				new String[] { "com.a.c.b.Class", "com.a.c.b.Class" },
				new String[] { "String", "String" } };
	}

	@DataProvider(name = "methods")
	Iterator<Object[]> getMethods() throws IOException {
		CSVReader reader = new CSVReader(new InputStreamReader(getClass()
				.getResourceAsStream("/methods.csv")), '|');
		List<String[]> inputList = reader.readAll();
		List<Object[]> outputList = new ArrayList<Object[]>();
		for (String[] strings : inputList) {
			MethodDef method = new MethodDef();
			method.setName(strings[0]);
			Object[] objects = new Object[strings.length];
			objects[0] = method;
			for (int i = 1; i < strings.length; i++) {
				objects[i] = strings[i];
			}
			outputList.add(objects);
		}
		return outputList.iterator();
	}
}
