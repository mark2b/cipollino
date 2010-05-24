package org.cipollino.core.xml;

import org.cipollino.core.annotations.ModelFactories;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;

@Singleton
public class X2JModelFactoryFactory {

	@Inject
	Injector injector;

	public AbstractX2JModelFactory getFactory(Object o) {
		if (o == null) {
			return null;
		}
		System.out.println("X2JModelFactoryFactory.getFactory() " + o);
		Package pckg = o.getClass().getPackage();
		String className = pckg.getName() + ".ModelFactory";
		System.out.println("X2JModelFactoryFactory.getFactory() " + className);
		AbstractX2JModelFactory factory = injector.getInstance(Key.get(AbstractX2JModelFactory.class, ModelFactories.named(className)));
		return factory;
	}
}
