package org.cipollino.logger.format;

public class ToString {

	private StringBuilder builder = new StringBuilder();

	private ObjectTreeVisitor visitor = new ObjectTreeVisitor(builder);

	private ToString() {
	}

	public static ToString create() {
		return new ToString();
	};

	public String toString(Object object) {
		visitor.visit(object);
		return builder.toString();
	}

	public ToString formatted(boolean formatted) {
		visitor.setFormatted(formatted);
		return this;
	}

	public ToString useToString(boolean useToString) {
		visitor.setUseToString(useToString);
		return this;
	}

	public ToString printParentFields(boolean printParentFields) {
		visitor.setPrintParentFields(printParentFields);
		return this;
	}
}
