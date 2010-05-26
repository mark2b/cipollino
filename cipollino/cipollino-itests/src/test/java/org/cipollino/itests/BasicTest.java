package org.cipollino.itests;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.testng.annotations.Test;

@Test
public class BasicTest extends AbstractTest {

	@Test
	public void test1() throws Exception {

		File jar = new File("target/app1.jar");
		assertTrue(jar.exists());

		Process process = startProcess("-jar", jar.getAbsolutePath());
		String pid = getProcesses().get(jar.getAbsolutePath());
		assertNotNull(pid);

		File jar2 = new File(
				"../cipollino-build/target/output/cipollino/lib/cipollino-agent-0.2-SNAPSHOT.jar");
		assertTrue(jar2.exists());

		startProcess("-jar", jar2.getAbsolutePath(), "--pid", pid, "--file",
				"src/test/resources/app1-file.xml");

		Thread.sleep(2000);
		process.destroy();
	}

}
