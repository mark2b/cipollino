package com.intellibird.cipollino.agent.test;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import com.google.cipollino.agent.Agent;
import com.google.cipollino.core.error.ErrorCode;
import com.google.cipollino.core.error.Status;
import com.google.cipollino.core.runtime.StartOptions;

public class AgentArgsParsingTest {

	@Test
	public void parseArgumentsTest() {
		TestAgent agent = new TestAgent();

		Status status = agent.testParseArgs("");
		assertTrue(status.isSuccess());

		status = agent.testParseArgs("--file=control.file.xml");
		assertFalse(status.isSuccess());
		assertEquals(status.getCode(), ErrorCode.ControlFileNotFound.getCode());

		status = agent.testParseArgs("--file=src/test/resources/control-file.xml");
		assertTrue(status.isSuccess());

		status = agent.testParseArgs("--fil=control.file.xml");
		assertFalse(status.isSuccess());
		assertEquals(status.getCode(), ErrorCode.ArgumentsParsingError.getCode());
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
