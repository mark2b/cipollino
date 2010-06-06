package org.cipollino.core.test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.*;
import static org.testng.Assert.assertTrue;

import java.io.InputStreamReader;

import org.cipollino.core.DIModule;
import org.cipollino.core.actions.Action;
import org.cipollino.core.actions.DefaultAction;
import org.cipollino.core.model.ActionDef;
import org.cipollino.core.model.Agent;
import org.cipollino.core.schema.ActionType;
import org.cipollino.core.schema.AgentType;
import org.cipollino.core.schema.TestActionType;
import org.cipollino.core.schema.X2JModelFactory;
import org.cipollino.core.xml.AbstractX2JModelFactory;
import org.cipollino.core.xml.ModelSerializer;
import org.cipollino.core.xml.X2JModelFactoryFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class X2JModelFactoryTest {

	@Inject
	X2JModelFactoryFactory x2jModelFactoryFactory;

	@Inject
	ModelSerializer modelSerializer;

	@Inject
	X2JModelFactory modelFactory;

	@BeforeClass
	void init() {
		Injector injector = Guice.createInjector(new DIModule());
		injector.injectMembers(this);
	}

	@Test(enabled = false)
	public void getFactoryTest() {
		AbstractX2JModelFactory factory = x2jModelFactoryFactory
				.getFactory(null);
		assertNull(factory);

		ActionType actionType = new ActionType();
		factory = x2jModelFactoryFactory.getFactory(actionType);
		assertNotNull(factory);
		assertTrue(factory instanceof X2JModelFactory);
	}

	@Test(enabled = false)
	public void parseTest() {
		AgentType agentType = modelSerializer.read(new InputStreamReader(
				getClass().getResourceAsStream("/control-file1.xml")),
				AgentType.class);
		assertNotNull(agentType);
		Agent project = modelFactory.create(agentType);
		Action action = project.getTargets().get(0).getActions().get(0)
				.createAction();
		assertTrue(action instanceof DefaultAction);
	}

	@Test
	public void createModelInvalidTypeTest() {
		try {
			modelFactory.createModel(new TestActionType(), String.class);
			fail();
		} catch (Exception e) {
			assertTrue(e.getCause() != null
					&& e.getCause() instanceof NoSuchMethodException);
		}
	}
}
