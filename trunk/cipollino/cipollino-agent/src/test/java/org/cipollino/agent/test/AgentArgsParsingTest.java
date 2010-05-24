package org.cipollino.agent.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.cipollino.agent.Agent;
import org.cipollino.agent.error.ErrorCode;
import org.cipollino.core.error.Status;
import org.cipollino.core.runtime.StartOptions;
import org.testng.annotations.Test;


public class AgentArgsParsingTest {

	@Test
	public void parseArgumentsTest() {
		TestAgent agent = new TestAgent();

		Status status = agent.testParseArgs("");
		assertFalse(status.isSuccess());

		status = agent.testParseArgs("--file=control.file.xml");
		assertFalse(status.isSuccess());
		assertEquals(status.getCode(), ErrorCode.ControlFileNotFound.getCode());

		status = agent
				.testParseArgs("--file=src/test/resources/control-file.xml");
		assertTrue(status.isSuccess());

		status = agent.testParseArgs("--fil=control.file.xml");
		assertFalse(status.isSuccess());
		assertEquals(status.getCode(), ErrorCode.ArgumentsParsingError
				.getCode());
	}
}

class TestAgent extends Agent {

	Status testParseArgs(String argsLine) {
		return parseArgs(argsLine);
	}

	StartOptions getOptions() {
		return options;
	}
}
