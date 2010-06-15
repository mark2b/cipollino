package org.cipollino.core.error;

public interface ErrorMessage {
	void print(Object... args);

	String format(Object... args);

	String getCode();

}
