package org.cipollino.core.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.cipollino.core.DIModule;
import org.cipollino.core.services.ConfigurationService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class ConfigurationServiceTest {

	@Inject
	ConfigurationService configurationService;

	@BeforeClass
	void init() {
	}

	@Test(enabled = true)
	public void readDependencyTest() {
		Injector injector = Guice.createInjector(new DIModule());
		injector.injectMembers(this);
		List<String> list = configurationService.getDependencies();
		assertEquals(list.size(), 1);
		assertTrue(list.get(0).endsWith("/lib/cipollino-logger.jar"));
	}
}
