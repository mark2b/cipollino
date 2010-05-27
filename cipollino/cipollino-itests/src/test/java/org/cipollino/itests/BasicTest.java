package org.cipollino.itests;

import static org.testng.Assert.*;

import java.io.File;
import java.util.List;

import org.testng.annotations.Test;

@Test
public class BasicTest extends AbstractTest {

	@Test(enabled = false)
	public void testJavaAgent() throws Exception {

		File jar = new File("target/app1.jar");
		File log = new File("target/app1.log");

		assertTrue(jar.exists());

		File jar2 = new File(getProductLib(), "cipollino-agent-0.2-SNAPSHOT.jar");

		assertTrue(jar2.exists());

		Process process = startJavaProcess("-javaagent:" + jar2.getAbsolutePath() + "=--file=src/test/resources/app1-file.xml", "-Dlog.file="
				+ log.getAbsolutePath(), "-jar", jar.getAbsolutePath());

		Thread.sleep(1000);
		process.destroy();
		List<String> logLines = loadLog(log);
		assertTrue(logLines.get(0).endsWith("Agent was started."));
	}

	@Test
	public void testAgentConnect() throws Exception {

		File jar = new File("target/app2.jar");
		File log = new File("target/app2.log");
		File log2 = new File("target/cipollino.log");

		assertTrue(jar.exists());

		File jar2 = new File(getProductLib(), "cipollino-agent-0.2-SNAPSHOT.jar");

		assertTrue(jar2.exists());

		Process process = startJavaProcess("-Dlog.file=" + log.getAbsolutePath(), "-jar", jar.getAbsolutePath());
		String pid = getProcesses().get(jar.getAbsolutePath());
		assertNotNull(pid);
		Thread.sleep(1000);

		Process agentProcess = startJavaProcess("-Dcipollino.log.file=" + log2.getAbsolutePath(), "-jar", jar2.getAbsolutePath(), "--file",
				"src/test/resources/app2-file.xml", "--pid", pid);

		Thread.sleep(1000);
		assertEquals(agentProcess.exitValue(), 0);
		process.destroy();
		Thread.sleep(1000);
		List<String> logLines = loadLog(log);
		assertTrue(logLines.get(2).endsWith("Agent was started."));
		List<String> log2Lines = loadLog(log2);
		assertTrue(log2Lines.get(0).endsWith("Connected."));
	}
}
