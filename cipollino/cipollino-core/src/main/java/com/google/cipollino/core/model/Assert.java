package com.google.cipollino.core.model;

public class Assert {

	private AssertType type;

	private String actual;

	private String expected;

	public AssertType getType() {
		return type;
	}

	public void setType(AssertType type) {
		this.type = type;
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}
}
