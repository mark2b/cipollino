package org.cipollino.itests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

@Test
public class CoreTest extends AbstractTest {

	@Test(enabled = false)
	public void testJavaAgent() throws Exception {

		ProcessContext context = startTestAppWithAgent("target/app1.jar",
				"target/app1.log", "src/test/resources/app1-file1.xml");

		Thread.sleep(3000);
		context.process.destroy();
		// List<String> logLines = loadLog(context.log);
		// assertTrue(logLines.get(0).endsWith("Agent was started."));
	}

	@Test(enabled = true)
	public void testJavaAgent2() throws Exception {

		ProcessContext context = startTestAppWithAgent("target/app1.jar",
				"target/app1.log", "src/test/resources/app1-file2.xml");

		Thread.sleep(3000);
		context.process.destroy();
		// List<String> logLines = loadLog(context.log);
		// assertTrue(logLines.get(0).endsWith("Agent was started."));
	}
}
