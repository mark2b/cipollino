package org.cipollino.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class DI {

	private static Injector rootInjector;

	public static Injector createInjector(Module... modules) {
		Injector injector = Guice.createInjector(modules);
		rootInjector = injector;
		return injector;
	}

	public static Injector getRootInjector() {
		return rootInjector;
	}
}
