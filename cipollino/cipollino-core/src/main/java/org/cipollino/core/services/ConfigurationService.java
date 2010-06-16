package org.cipollino.core.services;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import com.google.inject.Singleton;

@Singleton
public class ConfigurationService {

	private static ConfigurationService instance;

	private File homeDir = null;

	private File binDir = null;

	private File logDir = null;

	private File libDir = null;

	private File confDir = null;

	private CompositeConfiguration config = new CompositeConfiguration();

	private ConfigurationService() {
		config.setProperty("conf", getConf().getAbsolutePath());
		config.setProperty("lib", getLib().getAbsolutePath());
		loadSystemConfiguration();
		loadInternalConfiguration();
		loadExternalConfiguration();
	}

	private void loadSystemConfiguration() {
		config.addConfiguration(new SystemConfiguration());
	}

	private void loadInternalConfiguration() {
		try {
			URL url = ConfigurationService.class
					.getResource("/internal-config.xml");
			XMLConfiguration configuration = new XMLConfiguration();
			configuration.load(url);
			config.addConfiguration(configuration);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadExternalConfiguration() {
		try {
			URL url = ConfigurationService.class
					.getResource("/cipollino-config.xml");
			if (url != null) {
				XMLConfiguration configuration = new XMLConfiguration();
				configuration.load(url);
				config.addConfiguration(configuration);
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	synchronized public static ConfigurationService createService() {
		if (ConfigurationService.instance == null) {
			ConfigurationService.instance = new ConfigurationService();
		}
		return ConfigurationService.instance;
	}

	public File getHome() {
		if (homeDir == null) {
			String home = System.getProperty("cipollino.home", System
					.getProperty("user.dir"));
			homeDir = new File(home);
		}
		return homeDir;
	}

	public File getBin() {
		if (binDir == null) {
			binDir = new File(getHome(), "bin");
		}
		return confDir;
	}

	public File getConf() {
		if (confDir == null) {
			confDir = new File(getHome(), "conf");
		}
		return confDir;
	}

	public File getLib() {
		if (libDir == null) {
			libDir = new File(getHome(), "lib");
		}
		return libDir;
	}

	public File getLog() {
		if (logDir == null) {
			logDir = new File(getHome(), "log");
		}
		return logDir;
	}

	@SuppressWarnings("unchecked")
	public List<String> getDependencies() {
		return config.getList("dependency");
	}
}
