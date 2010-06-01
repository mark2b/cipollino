package org.cipollino.core.test;

import java.io.IOException;
import java.util.Properties;

import com.google.inject.AbstractModule;

public class DIModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			bind(Properties.class).toInstance(loadTestProperties());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private Properties loadTestProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/test.properties"));
		return properties;
	}
}
