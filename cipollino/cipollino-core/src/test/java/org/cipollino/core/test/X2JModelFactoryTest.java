package org.cipollino.core.test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.cipollino.core.DIModule;
import org.cipollino.core.schema.ActionType;
import org.cipollino.core.schema.TestActionType;
import org.cipollino.core.schema.X2JModelFactory;
import org.cipollino.core.xml.AbstractX2JModelFactory;
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
	X2JModelFactory modelFactory;

	@BeforeClass
	void init() {
		Injector injector = Guice.createInjector(new DIModule());
		injector.injectMembers(this);
	}

	@Test
	public void getFactoryTest() {
		AbstractX2JModelFactory factory = x2jModelFactoryFactory
				.getFactory(null);
		assertNull(factory);

		ActionType actionType = new ActionType();
		factory = x2jModelFactoryFactory.getFactory(actionType);
		assertNotNull(factory);
		assertTrue(factory instanceof X2JModelFactory);
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
