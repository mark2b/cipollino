package com.intellibird.cipollino.core.xml;

import java.lang.reflect.Method;

import com.google.inject.Inject;

abstract public class AbstractX2JModelFactory {

	@Inject
	protected ModelSerializer modelSerializer;

	@Inject
	protected X2JModelFactoryFactory modelFactoryFactory;

	public X2JModelFactoryFactory getModelFactoryFactory() {
		return modelFactoryFactory;
	}

	@SuppressWarnings("unchecked")
	private <T> T create(Object source, Class<T> targetClazz) {
		Method method;
		try {
			method = getClass().getMethod("create", source.getClass());
			return (T) method.invoke(this, source);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T createModel(Object source, Class<T> targetClass) {
		if (source == null) {
			return null;
		}
		AbstractX2JModelFactory modelFactory = getModelFactoryFactory().getFactory(source);
		return modelFactory.create(source, targetClass);
	}
}
