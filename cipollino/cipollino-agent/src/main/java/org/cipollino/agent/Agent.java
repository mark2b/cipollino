package org.cipollino.agent;

import static org.cipollino.agent.error.ErrorCode.AgentAlreadyConnected;
import static org.cipollino.agent.error.ErrorCode.AgentWasStarted;
import static org.cipollino.agent.error.ErrorCode.AgentWasnotStarted;
import static org.cipollino.agent.error.ErrorCode.ArgumentsParsingError;
import static org.cipollino.agent.error.ErrorCode.ControlFileMissing;
import static org.cipollino.agent.error.ErrorCode.ControlFileNotFound;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.cipollino.core.DI;
import org.cipollino.core.error.ErrorCode;
import org.cipollino.core.error.ErrorException;
import org.cipollino.core.runtime.StartOptions;
import org.cipollino.core.services.ConfigurationService;
import org.cipollino.core.services.PropertiesService;
import org.cipollino.core.services.TransformationService;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

public class Agent {

	private static final String OPTION_FILE = "file";

	protected final StartOptions options = new StartOptions();

	@Inject
	private TransformationService transformationService;

	private void start(String argsLine, Instrumentation instrumentation,
			boolean attached) {
		try {
			if (isAlreadyLoaded()) {
				throw new ErrorException(AgentAlreadyConnected);
			}
			loadDependencies(instrumentation);

			initDI();
			buildArgsOptions();
			parseArgs(argsLine);
			options.setAttached(attached);
			transformationService.start(instrumentation, options);
			registerAsLoaded();
			AgentWasStarted.print();
		} catch (final ErrorException e) {
			e.getErrorMessage().print(e.getArgs());
			AgentWasnotStarted.print();
		} catch (final Exception e) {
			ErrorCode.InternalError.print(e.getMessage(), e);
			AgentWasnotStarted.print();
		}
	}

	private void loadDependencies(Instrumentation instrumentation)
			throws IOException {
		ConfigurationService configurationService = ConfigurationService
				.createService();
		List<String> dependencies = configurationService.getDependencies();
		for (String dependency : dependencies) {
			File file = new File(dependency);
			if (file.exists()) {
				instrumentation.appendToSystemClassLoaderSearch(new JarFile(
						file));
			}
		}
	}

	private void initDI() throws Exception {
		Module[] modules = loadModules();
		final Injector injector = DI.createInjector(modules);
		injector.injectMembers(this);
	}

	private Module[] loadModules() throws Exception {
		PropertiesService propertiesService = new PropertiesService();
		List<String> modulesClassNames = propertiesService
				.getProperties("di.module");
		Module[] modules = new Module[modulesClassNames.size()];
		Iterator<String> classNameIterator = modulesClassNames.iterator();
		for (int i = 0; i < modules.length; i++) {
			modules[i] = (Module) getClass().getClassLoader().loadClass(
					classNameIterator.next()).newInstance();

		}
		return modules;
	}

	protected void parseArgs(String argsLine) {
		if (argsLine == null) {
			argsLine = "";
		}
		final String[] args = argsLine.split("&");
		final CommandLineParser parser = new PosixParser();
		try {
			final Options argsOptions = buildArgsOptions();
			final CommandLine cl = parser.parse(argsOptions, args);
			if (cl.hasOption(OPTION_FILE)) {
				final String fileName = cl.getOptionValue(OPTION_FILE);
				final File file = new File(fileName);
				if (!file.exists()) {
					throw new ErrorException(ControlFileNotFound, fileName);
				} else {
					options.setControlFile(file);
				}
			} else {
				throw new ErrorException(ControlFileMissing);
			}
		} catch (final ParseException e) {
			throw new ErrorException(ArgumentsParsingError, e, e.getMessage());
		}
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		main(agentArgs, inst, false);
	}

	public static void agentmain(String agentArgs, Instrumentation inst) {
		main(agentArgs, inst, true);
	}

	private static void main(String args, Instrumentation inst, boolean attach) {
		final Agent agent = new Agent();
		agent.start(args, inst, attach);
	}

	@SuppressWarnings("static-access")
	private Options buildArgsOptions() {
		final Options argsOptions = new Options();
		argsOptions.addOption(OptionBuilder.hasArg().withLongOpt(OPTION_FILE)
				.withDescription("Control File").create('f'));
		return argsOptions;
	}

	private boolean isAlreadyLoaded() {
		return System.getProperty("org.cipollino.agent") != null;
	}

	private void registerAsLoaded() {
		System.setProperty("org.cipollino.agent", "connected");
	}

}
