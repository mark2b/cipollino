package org.cipollino.core.test;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.cipollino.core.model.ClassPathDef;
import org.cipollino.core.services.ClassPathService;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class ClassPathServcieTest {

	@Inject
	Properties properties;

	@Inject
	ClassPathService classPathService;

	@Inject
	ClassPool classPool;

	@BeforeClass
	void init() {
		Injector injector = Guice.createInjector(new DIModule(),
				new org.cipollino.core.DIModule());
		injector.injectMembers(this);
	}

	@Test
	public void updateClassPoolTest() throws NotFoundException {
		String jarName = properties.getProperty("maven-compiler-plugin.path");
		assertNotNull(jarName);
		File jar = new File(jarName);
		assertTrue(jar.exists());
		ClassPathDef classPathDef = new ClassPathDef();
		classPathDef.getDir().add(jar.getParentFile().getAbsolutePath());
		// classPathDef.getJar().add(jar.getAbsolutePath());
		classPathService.updateClassPool(classPathDef);
		CtClass ctClass = classPool.get("org.apache.maven.plugin.CompilerMojo");
	}
}
