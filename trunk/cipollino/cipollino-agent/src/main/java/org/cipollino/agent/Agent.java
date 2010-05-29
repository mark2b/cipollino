package org.cipollino.agent;

import static org.cipollino.agent.error.ErrorCode.AgentWasStarted;
import static org.cipollino.agent.error.ErrorCode.AgentWasnotStarted;
import static org.cipollino.agent.error.ErrorCode.ArgumentsParsingError;
import static org.cipollino.agent.error.ErrorCode.ControlFileMissing;
import static org.cipollino.agent.error.ErrorCode.ControlFileNotFound;

import java.io.File;
import java.lang.instrument.Instrumentation;

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
import org.cipollino.core.services.TransformationService;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class Agent {

	private static final String OPTION_FILE = "file";

	protected final StartOptions options = new StartOptions();

	@Inject
	private TransformationService transformationService;

	private void start(String argsLine, Instrumentation instrumentation,
			boolean attached) {
		try {
			initDI();
			buildArgsOptions();
			parseArgs(argsLine);
			options.setAttached(attached);
			transformationService.start(instrumentation, options);
			AgentWasStarted.print();
		} catch (ErrorException e) {
			e.getErrorMessage().print(e.getArgs());
			AgentWasnotStarted.print();
		} catch (Exception e) {
			ErrorCode.InternalError.print(e.getMessage(), e);
			AgentWasnotStarted.print();
		}
	}

	private void initDI() {
		Injector injector = DI.createInjector(
				new org.cipollino.core.DIModule(),
				new org.cipollino.logger.DIModule());
		injector.injectMembers(this);
	}

	protected void parseArgs(String argsLine) {
		if (argsLine == null) {
			argsLine = "";
		}
		String[] args = argsLine.split("&");
		CommandLineParser parser = new PosixParser();
		try {
			Options argsOptions = buildArgsOptions();
			CommandLine cl = parser.parse(argsOptions, args);
			if (cl.hasOption(OPTION_FILE)) {
				String fileName = cl.getOptionValue(OPTION_FILE);
				File file = new File(fileName);
				if (!file.exists()) {
					throw new ErrorException(ControlFileNotFound, fileName);
				} else {
					options.setControlFile(file);
				}
			} else {
				throw new ErrorException(ControlFileMissing);
			}
		} catch (ParseException e) {
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
		Agent agent = new Agent();
		agent.start(args, inst, attach);
	}

	@SuppressWarnings("static-access")
	private Options buildArgsOptions() {
		Options argsOptions = new Options();
		argsOptions.addOption(OptionBuilder.hasArg().withLongOpt(OPTION_FILE)
				.withDescription("Control File").create('f'));
		return argsOptions;
	}
}
