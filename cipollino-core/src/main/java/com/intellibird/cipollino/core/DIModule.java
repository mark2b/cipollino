package com.intellibird.cipollino.core;

import javassist.ClassPool;

import com.google.inject.AbstractModule;
import com.intellibird.cipollino.core.annotations.ModelFactories;
import com.intellibird.cipollino.core.inst.ClassTransformer;
import com.intellibird.cipollino.core.parsers.MethodParser;
import com.intellibird.cipollino.core.runtime.Runtime;
import com.intellibird.cipollino.core.services.ControlFileService;
import com.intellibird.cipollino.core.services.PropertiesService;
import com.intellibird.cipollino.core.services.TransformationService;
import com.intellibird.cipollino.core.xml.AbstractX2JModelFactory;
import com.intellibird.cipollino.core.xml.ModelSerializer;

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
				ModelFactories.named("com.intellibird.cipollino.core.schema.ModelFactory")).to(
				com.intellibird.cipollino.core.schema.X2JModelFactory.class);
	}
}
