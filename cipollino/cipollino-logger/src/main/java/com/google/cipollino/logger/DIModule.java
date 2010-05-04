package com.google.cipollino.logger;

import com.google.cipollino.core.annotations.ModelFactories;
import com.google.cipollino.core.xml.AbstractX2JModelFactory;
import com.google.inject.AbstractModule;

public class DIModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AbstractX2JModelFactory.class).annotatedWith(
				ModelFactories.named("com.google.cipollino.logger.schema.ModelFactory")).to(
				com.google.cipollino.logger.schema.X2JModelFactory.class);
	}
}
