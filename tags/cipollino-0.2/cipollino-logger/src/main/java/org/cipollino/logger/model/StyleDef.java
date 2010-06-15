package org.cipollino.logger.model;

public class StyleDef {
	
	private boolean useToString = false;

	private boolean printParentFields = true;

	private boolean formatted = true;

	public boolean isUseToString() {
		return useToString;
	}

	public void setUseToString(boolean useToString) {
		this.useToString = useToString;
	}

	public boolean isPrintParentFields() {
		return printParentFields;
	}

	public void setPrintParentFields(boolean printParentFields) {
		this.printParentFields = printParentFields;
	}

	public boolean isFormatted() {
		return formatted;
	}

	public void setFormatted(boolean formatted) {
		this.formatted = formatted;
	}
}
