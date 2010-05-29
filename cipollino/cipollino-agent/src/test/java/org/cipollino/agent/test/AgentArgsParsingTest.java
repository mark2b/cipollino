package org.cipollino.agent.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.cipollino.agent.Agent;
import org.cipollino.agent.error.ErrorCode;
import org.cipollino.core.error.ErrorException;
import org.cipollino.core.runtime.StartOptions;
import org.testng.annotations.Test;

public class AgentArgsParsingTest {

	@Test
	public void parseArgumentsTest() {
		TestAgent agent = new TestAgent();
		try {
			agent.testParseArgs("");
			fail();
		} catch (ErrorException e) {
		}
		try {
			agent.testParseArgs("--file=control.file.xml");
			fail();
		} catch (ErrorException e) {
			assertEquals(e.getErrorMessage(), ErrorCode.ControlFileNotFound);
		}

		agent.testParseArgs("--file=src/test/resources/control-file.xml");

		try {
			agent.testParseArgs("--fil=control.file.xml");
			fail();
		} catch (ErrorException e) {
			assertEquals(e.getErrorMessage(), ErrorCode.ArgumentsParsingError);
		}
	}
}

class TestAgent extends Agent {

	void testParseArgs(String argsLine) {
		parseArgs(argsLine);
	}

	StartOptions getOptions() {
		return options;
	}
}
