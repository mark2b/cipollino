package org.cipollino.core.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStreamReader;

import org.cipollino.core.DIModule;
import org.cipollino.core.error.Status;
import org.cipollino.core.schema.ActionType;
import org.cipollino.core.schema.ProjectType;
import org.cipollino.core.schema.ScriptType;
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
		Status status = Status.createStatus();
		ProjectType projectType = modelSerializer.read(status, new InputStreamReader(getClass().getResourceAsStream("/control-file1.xml")), ProjectType.class);
		assertTrue(status.isSuccess());
		assertNotNull(projectType);
		assertEquals(projectType.getTarget().size(), 1);
		ActionType actionType = projectType.getTarget().get(0).getAction().get(0);
		ScriptType scriptType = actionType.getScript().get(0);
	}
}
