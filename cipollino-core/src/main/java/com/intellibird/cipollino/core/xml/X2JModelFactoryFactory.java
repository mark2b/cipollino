package com.intellibird.cipollino.core.xml;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.intellibird.cipollino.core.annotations.ModelFactories;

@Singleton
public class X2JModelFactoryFactory {

	@Inject
	Injector injector;

	public AbstractX2JModelFactory getFactory(Object o) {
		if (o == null) {
			return null;
		}
		Package pckg = o.getClass().getPackage();
		String className = pckg.getName() + ".ModelFactory";
		AbstractX2JModelFactory factory = injector.getInstance(Key.get(AbstractX2JModelFactory.class, ModelFactories
				.named(className)));
		return factory;
	}
}
