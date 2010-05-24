package com.google.cipollino.core.annotations;

public class ModelFactories {

	public static ModelFactory named(String name) {
		return new ModelFactoryImpl(name);
	}
}
