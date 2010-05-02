package com.intellibird.cipollino.logger;

import com.google.inject.AbstractModule;
import com.intellibird.cipollino.core.annotations.ModelFactories;
import com.intellibird.cipollino.core.xml.AbstractX2JModelFactory;

public class DIModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AbstractX2JModelFactory.class).annotatedWith(
				ModelFactories.named("com.intellibird.cipollino.logger.schema.ModelFactory")).to(
				com.intellibird.cipollino.logger.schema.X2JModelFactory.class);
	}
}
