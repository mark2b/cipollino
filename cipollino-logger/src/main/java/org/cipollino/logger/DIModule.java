package org.cipollino.logger;

import org.cipollino.core.annotations.ModelFactories;
import org.cipollino.core.xml.AbstractX2JModelFactory;

import com.google.inject.AbstractModule;

public class DIModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AbstractX2JModelFactory.class).annotatedWith(
				ModelFactories.named("org.cipollino.logger.schema.ModelFactory")).to(
				org.cipollino.logger.schema.X2JModelFactory.class);
	}
}
