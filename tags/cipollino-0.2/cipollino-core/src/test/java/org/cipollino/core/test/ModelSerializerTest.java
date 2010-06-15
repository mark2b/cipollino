package org.cipollino.core.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.InputStreamReader;

import org.cipollino.core.DIModule;
import org.cipollino.core.error.ErrorException;
import org.cipollino.core.schema.ActionType;
import org.cipollino.core.schema.SystemType;
import org.cipollino.core.xml.ModelSerializer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
		SystemType modelType = modelSerializer.read(new InputStreamReader(
				getClass().getResourceAsStream("/control-file1.xml")),
				SystemType.class);
		assertNotNull(modelType);
		assertEquals(modelType.getTarget().size(), 1);
		ActionType actionType = modelType.getTarget().get(0).getAction().get(0);
		actionType.getScript().get(0);
	}

	@Test(expectedExceptions = ErrorException.class, expectedExceptionsMessageRegExp = ".*(XML Parsing failed).*")
	public void testReadBroken() throws Exception {
		modelSerializer.read(new InputStreamReader(getClass()
				.getResourceAsStream("/control-file1-broken.xml")),
				SystemType.class);
	}
}
