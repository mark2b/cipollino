package com.intellibird.cipollino.core.test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStreamReader;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.cipollino.core.DIModule;
import com.google.cipollino.core.error.Status;
import com.google.cipollino.core.error.Status.Severity;
import com.google.cipollino.core.schema.DirectivesType;
import com.google.cipollino.core.xml.ModelSerializer;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class ModelSerializerTest {

	@Inject
	ModelSerializer modelSerializer;

	@BeforeClass
	void init() {
		Injector injector = Guice.createInjector(new DIModule());
		injector.injectMembers(this);
	}

	@Test
	public void testRead() throws Exception {
		Status status = Status.createStatus(Severity.SUCCESS);
		DirectivesType directivesType = modelSerializer.read(status, new InputStreamReader(getClass()
				.getResourceAsStream("/spy-config1.xml")), DirectivesType.class);
		assertTrue(status.isSuccess());
		assertNotNull(directivesType);
	}
}
