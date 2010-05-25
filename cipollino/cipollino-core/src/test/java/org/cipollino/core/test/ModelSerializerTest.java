package org.cipollino.core.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStreamReader;

import org.cipollino.core.DIModule;
import org.cipollino.core.error.Status;
import org.cipollino.core.schema.ActionType;
import org.cipollino.core.schema.AgentType;
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
		AgentType agentType = modelSerializer.read(status, new InputStreamReader(getClass().getResourceAsStream("/control-file1.xml")), AgentType.class);
		assertTrue(status.isSuccess());
		assertNotNull(agentType);
		assertEquals(agentType.getTarget().size(), 1);
		ActionType actionType = agentType.getTarget().get(0).getAction().get(0);
		actionType.getScript().get(0);
	}
}
