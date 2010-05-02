package com.intellibird.cipollino.core.services;

import java.io.IOException;
import java.util.Properties;

import com.google.inject.Singleton;

@Singleton
public class PropertiesService {

	private Properties properties = new Properties();

	public PropertiesService() {

		try {
			properties.load(getClass().getResourceAsStream("/cipollino.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getJaxbPath() {
		return (String) properties.get("jaxb.path");
	}
}
