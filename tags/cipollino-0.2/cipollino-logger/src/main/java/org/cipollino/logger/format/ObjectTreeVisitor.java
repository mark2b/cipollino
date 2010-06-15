package org.cipollino.logger.format;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ObjectTreeVisitor {

	final static private String NULL = "(null)";

	final private StringBuilder builder;

	private int level = 0;

	private boolean useToString = true;

	private boolean printParentFields = false;

	private boolean formatted = true;

	private Set<Object> nodes = new HashSet<Object>();

	public ObjectTreeVisitor(StringBuilder builder) {
		this.builder = builder;
	}

	public void visit(Object object) {

		try {
			if (null == object) {
				builder.append(NULL);
			} else if (object.getClass().isPrimitive()) {
				builder.append(object.toString());
			} else if (object instanceof String) {
				builder.append('"');
				builder.append(object.toString());
				builder.append('"');
			} else if (isJavaLang(object)) {
				builder.append(object.toString());
			} else if (useToString && isHasToString(object)) {
				builder.append(object.toString());
			} else {
				if (nodes.add(object)) {
					if (object.getClass().isArray()) {
						visitArray(object);
					} else if (object instanceof Iterable<?>) {
						visitIterable((Iterable<?>) object);
					} else if (object instanceof Map<?, ?>) {
						visitMap((Map<?, ?>) object);
					} else {
						visitMembers(object, object.getClass());
					}
				} else {
					builder.append(toBaseString(object));
				}
			}
		} catch (Exception e) {
		}
	}

	private boolean isHasToString(Object object) {
		try {
			Method method = object.getClass().getMethod("toString");
			return !method.getDeclaringClass().equals(Object.class);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return false;
	}

	private void visitIterable(Iterable<?> iterable) {
		builder.append(iterable.getClass().getSimpleName());
		builder.append(" {");
		Iterator<?> iterator = iterable.iterator();
		int l = builder.length();
		for (int i = 0; iterator.hasNext(); i++) {
			if (i > 0) {
				builder.append(",");
			}
			visit(iterator.next());
			if (builder.length() > l + 80) {
				printNewLine();
				l = builder.length();
			}
		}
		builder.append("}");
	}

	private void visitMap(Map<?, ?> map) {
		builder.append(map.getClass().getSimpleName());
		builder.append(" {");
		Iterator<?> iterator = map.entrySet().iterator();
		int l = builder.length();
		for (int i = 0; iterator.hasNext(); i++) {
			if (i > 0) {
				builder.append(",");
			}
			Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
			visit(entry.getKey());
			builder.append("=");
			visit(entry.getValue());
			if (builder.length() > l + 80) {
				printNewLine();
				l = builder.length();
			}
		}
		builder.append("}");
	}

	private void visitArray(Object array) {
		int length = Array.getLength(array);
		builder.append("{");
		int l = builder.length();
		for (int i = 0; i < length; i++) {
			if (i > 0) {
				builder.append(",");
			}
			Object value = Array.get(array, i);
			visit(value);
			if (builder.length() > l + 80) {
				printNewLine();
				l = builder.length();
			}
		}
		builder.append("}");
	}

	private void visitMembers(Object object, Class<?> type) {
		level++;
		if (level < 5) {
			Field[] fields = type.getDeclaredFields();
			visitMembers(fields, object, type);
			if (printParentFields) {
				Class<?> superClass = type.getSuperclass();
				while (superClass != null && superClass != Object.class) {
					fields = superClass.getDeclaredFields();
					visitMembers(fields, object, superClass);
					superClass = superClass.getSuperclass();
				}
			}
		}
		level--;
	}

	private void visitMembers(Field[] fields, Object object, Class<?> type) {
		AccessibleObject.setAccessible(fields, true);
		builder.append(type.getSimpleName());
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				printNewLine();
				builder.append(field.getName());
				builder.append(":");
				visitObjectValue(object, field);
			}
		}
	}

	private void printNewLine() {
		if (formatted) {
			builder.append("\n");
			printOffset();
		}
	}

	private void printOffset() {
		for (int i = 0; i < level; i++) {
			builder.append("    ");
		}
	}

	private void visitObjectValue(Object object, Field field) {
		try {
			Object value = field.get(object);
			visit(value);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
	}

	private boolean isJavaLang(Object objectToVisit) {
		return objectToVisit.getClass().getPackage() != null
				&& objectToVisit.getClass().getPackage().getName().equals(
						"java.lang");
	}

	private String toBaseString(Object object) {
		return object.getClass().getSimpleName() + "@"
				+ Integer.toHexString(hashCode());
	}

	@Override
	protected void finalize() throws Throwable {
		nodes.clear();
	}

	public void setUseToString(boolean useToString) {
		this.useToString = useToString;
	}

	public void setPrintParentFields(boolean printParentFields) {
		this.printParentFields = printParentFields;
	}

	public void setFormatted(boolean formatted) {
		this.formatted = formatted;
	}
}
