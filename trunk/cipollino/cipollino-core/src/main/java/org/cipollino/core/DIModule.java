package org.cipollino.core;

import org.cipollino.core.annotations.ModelFactories;
import org.cipollino.core.inst.ClassTransformer;
import org.cipollino.core.parsers.MethodParser;
import org.cipollino.core.runtime.Runtime;
import org.cipollino.core.services.ClassPathService;
import org.cipollino.core.services.ControlFileService;
import org.cipollino.core.services.PropertiesService;
import org.cipollino.core.services.ReplaceService;
import org.cipollino.core.services.TransformationService;
import org.cipollino.core.xml.AbstractX2JModelFactory;
import org.cipollino.core.xml.ModelSerializer;

import javassist.ClassPool;

import com.google.inject.AbstractModule;

public class DIModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ModelSerializer.class);
		bind(Runtime.class);
		bind(ClassTransformer.class);
		bind(MethodParser.class);
		bind(TransformationService.class);
		bind(PropertiesService.class);
		bind(ControlFileService.class);
		bind(ReplaceService.class);
		bind(ClassPathService.class);
		bind(AbstractX2JModelFactory.class).annotatedWith(ModelFactories.named("org.cipollino.core.schema.ModelFactory")).to(
				org.cipollino.core.schema.X2JModelFactory.class);
	}
}
