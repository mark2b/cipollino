package org.cipollino.core.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStreamReader;
import java.util.List;

import org.cipollino.core.DIModule;
import org.cipollino.core.schema.ActionType;
import org.cipollino.core.schema.ClassPathType;
import org.cipollino.core.schema.MethodType;
import org.cipollino.core.schema.PhaseType;
import org.cipollino.core.schema.ScriptType;
import org.cipollino.core.schema.SystemType;
import org.cipollino.core.schema.TargetType;
import org.cipollino.core.xml.ModelSerializer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class ModelTest {

	@Inject
	private ModelSerializer modelSerializer;

	// @Inject
	// private X2JModelFactory modelFactory;

	@BeforeClass
	void init() {
		Injector injector = Guice.createInjector(new DIModule());
		injector.injectMembers(this);
	}

	@Test
	public void phaseTypeTest() {
		assertEquals(PhaseType.BEFORE.value(), "before");

		assertEquals(PhaseType.fromValue("before"), PhaseType.BEFORE);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void phaseTypeInvalidTest() {
		PhaseType.fromValue("invalidValue");
	}

	@Test
	public void actionTypeTest() {
		ActionType actionType = new ActionType();
		List<ScriptType> scriptTypes = actionType.getScript();
		assertNotNull(scriptTypes);
		assertEquals(scriptTypes.size(), 0);

		assertEquals(PhaseType.fromValue("before"), PhaseType.BEFORE);
	}

	@Test
	public void parseTest() {
		SystemType modelType = modelSerializer.read(new InputStreamReader(
				getClass().getResourceAsStream("/control-file1.xml")),
				SystemType.class);
		assertNotNull(modelType);

		ClassPathType classPathType = modelType.getCompiler().getClassPath();

		assertEquals(classPathType.getClasses().get(0), "/classes");
		assertEquals(classPathType.getDir().get(0), "/tmp");
		assertEquals(classPathType.getJar().get(0), "/tmp/a.jar");
		assertEquals(classPathType.getPath().get(0), "a.jar:b.jar");

		List<TargetType> targetTypes = modelType.getTarget();
		TargetType targetType = targetTypes.get(0);

		MethodType methodType = targetType.getMethod().get(0);

		assertEquals(methodType.getName(), "a");

		ActionType actionType = targetType.getAction().get(0);
		ScriptType scriptType = actionType.getScript().get(0);

		assertEquals(scriptType.getPhase(), PhaseType.BEFORE);
		assertTrue(scriptType.getValue().length() > 0);

		assertEquals(scriptType.getAssignTo(), "v1");

		ScriptType scriptType1 = actionType.getScript().get(1);
		assertEquals(scriptType1.getPhase(), PhaseType.AFTER);

		// Agent project = modelFactory.create(agentType);
		//
		// ActionDef actionDef = modelFactory.createModel(actionType,
		// ActionDef.class);
		//
		// Action action = actionDef.createAction();
		//
		// assertTrue(action instanceof DefaultAction);
	}
}
