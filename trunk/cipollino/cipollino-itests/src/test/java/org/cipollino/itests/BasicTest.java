package org.cipollino.itests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

@Test
public class BasicTest extends AbstractTest {

	@Test(enabled = true)
	public void testJavaAgent() throws Exception {

		ProcessContext context = startTestAppWithAgent("target/app1.jar",
				"target/app1.log", "src/test/resources/app1-file.xml");

		Thread.sleep(3000);
		context.process.destroy();
		List<String> logLines = loadLog(context.log);
		assertTrue(logLines.get(0).endsWith("Agent was started."));
	}

	@Test(enabled = true)
	public void testAgentConnectWithWrongProcess() throws Exception {

		ProcessContext agentContext = startAgentApp(
				"src/test/resources/app2-file.xml", "1234");

		Thread.sleep(2000);
		int exitValue = waitForProcessExit(agentContext.process);
		assertEquals(exitValue, 201);
	}

	@Test(enabled = true)
	public void testAgentConnect() throws Exception {

		ProcessContext appContext = startTestApp("target/app2.jar",
				"target/app2.log");

		Thread.sleep(1000);

		ProcessContext agentContext = startAgentApp(
				"src/test/resources/app2-file.xml", appContext.pid);

		Thread.sleep(2000);
		int exitValue = waitForProcessExit(agentContext.process);
		assertEquals(exitValue, 0);

		Thread.sleep(1000);
		appContext.process.destroy();

		List<String> appLogLines = loadLog(appContext.log);
		assertTrue(findInLog(appLogLines, "Agent was started."));

		List<String> agentLogLines = loadLog(agentContext.log);
		assertTrue(findInLog(agentLogLines, "Connected."));
	}

	@Test(enabled = true)
	public void testAgentAlreadyConnected() throws Exception {

		ProcessContext appContext = startTestApp("target/app2.jar",
				"target/app2.log");

		Thread.sleep(1000);

		ProcessContext agentContext = startAgentApp(
				"src/test/resources/app2-file.xml", appContext.pid);

		Thread.sleep(1000);
		int exitValue = waitForProcessExit(agentContext.process);
		assertEquals(exitValue, 0);

		List<String> appLogLines = loadLog(appContext.log);
		assertTrue(findInLog(appLogLines, "Agent was started."));

		List<String> agentLogLines = loadLog(agentContext.log);
		assertTrue(findInLog(agentLogLines, "Connected."));

		startAgentApp("src/test/resources/app2-file.xml", appContext.pid);
		Thread.sleep(2000);

		appLogLines = loadLog(appContext.log);
		assertTrue(findInLog(appLogLines, "(00211)"));

		Thread.sleep(500);
		appContext.process.destroy();

	}
}
