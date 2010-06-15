package org.cipollino.core.test;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.cipollino.core.services.ReplaceService;
import org.testng.annotations.Test;


public class ReplaceServiceTest {

	ReplaceService replaceService = new ReplaceService();

	@Test
	public void replacePropertyTest() {
		Properties properties = new Properties();
		properties.setProperty("a", "A");
		properties.setProperty("bb", "BB");
		properties.setProperty("ccc", "CCC");
		String result = replaceService.replaceByProperties("+ ${a} + ${a} +", properties);
		assertEquals(result, "+ A + A +");
		result = replaceService.replaceByProperties("+ ${a} + ${bb} + ${ccc} +", properties);
		assertEquals(result, "+ A + BB + CCC +");
		result = replaceService.replaceByProperties("${a}", properties);
		assertEquals(result, "A");
		result = replaceService.replaceByProperties("${Z}", properties);
		assertEquals(result, "${Z}");
	}
}
