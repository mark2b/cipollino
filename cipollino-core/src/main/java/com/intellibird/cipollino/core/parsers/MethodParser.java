package com.intellibird.cipollino.core.parsers;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.inject.Singleton;
import com.intellibird.cipollino.core.error.ErrorCode;
import com.intellibird.cipollino.core.exception.ErrorException;
import com.intellibird.cipollino.core.model.MethodDef;

@Singleton
public class MethodParser {

	final public static String CLASS_PATTERN = "((?:\\w+\\.)*\\w+)";

	final public static String METHOD_PATTERN = "\\s*" + CLASS_PATTERN + "\\s+" + CLASS_PATTERN + "\\.(\\w+)\\s*\\(("
			+ CLASS_PATTERN + "\\s+(\\w+)\\s*\\,)*\\s*(" + CLASS_PATTERN + "\\s+(\\w+))?\\)";

	final Pattern PATTERN = Pattern.compile(METHOD_PATTERN);

	public void parseMethod(MethodDef method) {
		String input = method.getName();
		Matcher matcher = PATTERN.matcher(input);
		if (matcher.find()) {
			String returnType = matcher.group(1);
			if (returnType == null) {
				throw new ErrorException(ErrorCode.ReturnTypeNotFound, input);
			}
			method.setReturnType(returnType);
			String className = matcher.group(2);
			if (className == null) {
				throw new ErrorException(ErrorCode.ClassNameNotFound, input);
			}
			method.setClassName(className);
			String methodName = matcher.group(3);
			if (methodName == null) {
				throw new ErrorException(ErrorCode.MethodNameNotFound, input);
			}
			method.setMethodName(methodName);
			for (int i = 5; i <= matcher.groupCount(); i += 3) {
				if (matcher.groupCount() >= i + 1) {
					String argClassName = matcher.group(i);
					String argName = matcher.group(i + 1);
					if (argClassName != null && argName != null) {
						method.getArguments().put(argName, argClassName);
					}
				}
			}
			normalizeMethod(method);
		} else {
			throw new ErrorException(ErrorCode.InvalidMethod, input);
		}
	}

	private void normalizeMethod(MethodDef method) {
		method.setReturnType(normalizeClassName(method.getReturnType()));
		method.setClassName(normalizeClassName(method.getClassName()));
		for (Entry<String, String> entry : method.getArguments().entrySet()) {
			method.getArguments().put(entry.getKey(), normalizeClassName(entry.getValue()));
		}
	}

	private String normalizeClassName(String className) {
		if (className.indexOf('.') < 0) {
			if (Arrays.binarySearch(primitiveTypes, className) < 0) {
				className = "java.lang." + className;
			}
		}
		return className;
	}

	private String[] primitiveTypes = new String[] { Boolean.TYPE.getName(), Byte.TYPE.getName(),
			Character.TYPE.getName(), Double.TYPE.getName(), Float.TYPE.getName(), Integer.TYPE.getName(),
			Long.TYPE.getName(), Short.TYPE.getName(), Void.TYPE.getName() };

}
