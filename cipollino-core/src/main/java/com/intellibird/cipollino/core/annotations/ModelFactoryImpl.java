package com.intellibird.cipollino.core.annotations;

import java.io.Serializable;
import java.lang.annotation.Annotation;

class ModelFactoryImpl implements ModelFactory, Serializable {

	private static final long serialVersionUID = 1L;

	final private String value;

	public ModelFactoryImpl(String value) {
		this.value = value;
	}

	@Override
	public String value() {
		return value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return ModelFactory.class;
	}

	public int hashCode() {
		return (127 * "value".hashCode()) ^ value.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof ModelFactory)) {
			return false;
		}

		ModelFactory other = (ModelFactory) o;
		return value.equals(other.value());
	}
}
