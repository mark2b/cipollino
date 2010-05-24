package org.cipollino.agent.test;

import org.cipollino.core.DIModule;
import org.cipollino.core.services.PropertiesService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class PropertiesServiceTest {

	@Inject
	PropertiesService propertiesService;
	
	@BeforeClass
	void init() {
		Injector injector = Guice.createInjector(new DIModule());
		injector.injectMembers(this);
	}

	@Test
	public void jaxbPathTest() {
		System.out.println(propertiesService.getJaxbPath());
	}
}
