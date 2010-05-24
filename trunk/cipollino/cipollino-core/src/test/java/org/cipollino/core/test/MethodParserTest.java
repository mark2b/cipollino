package org.cipollino.core.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cipollino.core.model.MethodDef;
import org.cipollino.core.parsers.MethodParser;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;


public class MethodParserTest {

	MethodParser methodParser = new MethodParser();

	@Test(dataProvider = "classes", enabled = false)
	public void classPatternTest(String input, String expected) {
		Pattern pattern = Pattern.compile(MethodParser.CLASS_PATTERN);
		Matcher matcher = pattern.matcher(input);
		boolean found = matcher.find();
		assertTrue(found);
		assertEquals(matcher.group(1), expected);
	}

	@Test(dataProvider = "methods", enabled = true)
	public void methodPatternTest(MethodDef method, String className, String methodName,
			String params) {
		methodParser.parseMethod(method);
		assertEquals(method.getClassName(), className);
		assertEquals(method.getMethodName(), methodName);
		String[] paramsPairs = params.split("\\s*,\\s*");
		Iterator<Entry<String, String>> argumentsIt = method.getArguments().entrySet().iterator();
		for (String paramPair : paramsPairs) {
			Entry<String, String> argument = argumentsIt.next();
			String[] tokens = paramPair.split("\\s+");
			assertEquals(argument.getValue(), tokens[0]);
			assertEquals(argument.getKey(), tokens[1]);
		}
	}

	@DataProvider(name = "classes")
	Object[][] getClasses() {
		return new String[][] { new String[] { "com.a.c.b.Class", "com.a.c.b.Class" },
				new String[] { "String", "String" } };
	}

	@DataProvider(name = "methods")
	Iterator<Object[]> getMethods() throws IOException {
		CSVReader reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream("/methods.csv")), '|');
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
