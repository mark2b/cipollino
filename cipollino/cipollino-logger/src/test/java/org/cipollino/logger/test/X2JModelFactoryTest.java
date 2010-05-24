package org.cipollino.logger.test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStreamReader;

import org.cipollino.core.DIModule;
import org.cipollino.core.actions.Action;
import org.cipollino.core.error.Status;
import org.cipollino.core.model.Project;
import org.cipollino.core.schema.ProjectType;
import org.cipollino.core.schema.X2JModelFactory;
import org.cipollino.core.xml.ModelSerializer;
import org.cipollino.logger.actions.LoggerAction;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class X2JModelFactoryTest {

	@Inject
	ModelSerializer modelSerializer;

	@Inject
	X2JModelFactory modelFactory;

	@BeforeClass
	void init() {
		Injector injector = Guice.createInjector(new DIModule(), new org.cipollino.logger.DIModule());
		injector.injectMembers(this);
	}

	@Test
	public void parseTest() {
		Status status = Status.createStatus();
		ProjectType projectType = modelSerializer.read(status, new InputStreamReader(getClass().getResourceAsStream("/control-file1.xml")), ProjectType.class);
		assertTrue(status.isSuccess());
		assertNotNull(projectType);
		Project project = modelFactory.create(projectType);
		Action action = project.getTargets().get(0).getActions().get(0).createAction();
		assertTrue(action instanceof LoggerAction);
	}
}
