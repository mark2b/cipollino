package org.cipollino.core.services;

import static org.cipollino.core.error.ErrorCode.ClassNameNotFound;
import static org.cipollino.core.error.ErrorCode.InvalidMethod;
import static org.cipollino.core.error.ErrorCode.MethodNameNotFound;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cipollino.core.error.ErrorException;
import org.cipollino.core.model.MethodDef;
import org.cipollino.core.model.ParameterDef;

import com.google.inject.Singleton;

@Singleton
public class MethodParserService {

	final public static String CLASS_PATTERN = "((?:\\w+\\.)*\\w+)";

	final public static String METHOD_PATTERN = "\\s*" + CLASS_PATTERN
			+ "\\.(\\w+)\\s*\\((" + CLASS_PATTERN + "\\s+(\\w+)\\s*\\,)*\\s*("
			+ CLASS_PATTERN + "\\s+(\\w+))?\\)";

	final Pattern PATTERN = Pattern.compile(METHOD_PATTERN);

	public void parseMethod(MethodDef method) {
		String input = method.getName();
		Matcher matcher = PATTERN.matcher(input);
		if (matcher.find()) {
			String className = matcher.group(1);
			if (className == null) {
				throw new ErrorException(ClassNameNotFound, input);
			}
			method.setClassName(className);
			String methodName = matcher.group(2);
			if (methodName == null) {
				throw new ErrorException(MethodNameNotFound, input);
			}
			method.setMethodName(methodName);
			int parameterIndex = -1;
			for (int i = 4; i <= matcher.groupCount(); i += 3) {
				if (matcher.groupCount() >= i + 1) {
					String argClassName = matcher.group(i);
					String argName = matcher.group(i + 1);
					if (argClassName != null && argName != null) {
						parameterIndex++;
						ParameterDef parameterDef = method.getParameters().get(
								parameterIndex);
						if (parameterDef == null) {
							parameterDef = new ParameterDef();
							parameterDef.setIndex(parameterIndex);
							parameterDef.setName(argName);
							method.getParameters().put(parameterIndex,
									parameterDef);
						}
						parameterDef.setType(argClassName);
					}
				}
			}
			normalizeMethod(method);
		} else {
			throw new ErrorException(InvalidMethod, input);
		}
	}

	private void normalizeMethod(MethodDef method) {
		method.setClassName(normalizeClassName(method.getClassName()));
		for (Entry<Integer, ParameterDef> entry : method.getParameters()
				.entrySet()) {
			ParameterDef parameterDef = entry.getValue();
			parameterDef.setType(normalizeClassName(parameterDef.getType()));
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

	private String[] primitiveTypes = new String[] { Boolean.TYPE.getName(),
			Byte.TYPE.getName(), Character.TYPE.getName(),
			Double.TYPE.getName(), Float.TYPE.getName(),
			Integer.TYPE.getName(), Long.TYPE.getName(), Short.TYPE.getName(),
			Void.TYPE.getName() };

}
