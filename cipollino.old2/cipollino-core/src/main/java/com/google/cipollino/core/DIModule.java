package com.google.cipollino.core;

import javassist.ClassPool;

import com.google.cipollino.core.annotations.ModelFactories;
import com.google.cipollino.core.inst.ClassTransformer;
import com.google.cipollino.core.parsers.MethodParser;
import com.google.cipollino.core.runtime.Runtime;
import com.google.cipollino.core.services.ControlFileService;
import com.google.cipollino.core.services.PropertiesService;
import com.google.cipollino.core.services.TransformationService;
import com.google.cipollino.core.xml.AbstractX2JModelFactory;
import com.google.cipollino.core.xml.ModelSerializer;
import com.google.inject.AbstractModule;

public class DIModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ModelSerializer.class);
		bind(Runtime.class);
		bind(ClassTransformer.class);
		bind(MethodParser.class);
		bind(TransformationService.class);
		bind(ClassPool.class).toInstance(ClassPool.getDefault());
		bind(PropertiesService.class);
		bind(ControlFileService.class);
		bind(AbstractX2JModelFactory.class).annotatedWith(
				ModelFactories.named("com.google.cipollino.core.schema.ModelFactory")).to(
				com.google.cipollino.core.schema.X2JModelFactory.class);
	}
}
