package org.cipollino.core.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Singleton;

@Singleton
public class PropertiesService {

	private Map<String, String> properties = new HashMap<String, String>();

	private List<Properties> propertiesList = new ArrayList<Properties>();

	public PropertiesService() {
		try {
			Enumeration<URL> urls = getClass().getClassLoader().getResources(
					"cipollino.properties");
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				Properties properties = new Properties();
				InputStream inputStream;
				try {
					inputStream = url.openStream();
					properties.load(inputStream);
					inputStream.close();
				} catch (IOException e) {
				}
				propertiesList.add(properties);
			}
		} catch (IOException e1) {
		}
	}

	public String getJaxbPath() {
		return getProperty("jaxb.path");
	}

	public String getProperty(String key) {
		String value = properties.get(key);
		if (value == null) {
			value = handleKey(key);
		}
		return value;
	}

	private String handleKey(String key) {
		List<String> values = getProperties(key);
		if (key.equals("jaxb.path")) {
			return StringUtils.join(values.iterator(), ":");
		}
		return values.size() > 0 ? values.get(0) : null;
	}

	private List<String> getProperties(String key) {
		List<String> list = new ArrayList<String>();
		for (Properties properties : propertiesList) {
			String value = (String) properties.get(key);
			if (value != null) {
				list.add(value);
			}
		}
		return list;
	}
}
