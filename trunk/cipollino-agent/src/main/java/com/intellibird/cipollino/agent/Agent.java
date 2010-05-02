package com.intellibird.cipollino.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.intellibird.cipollino.core.error.ErrorCode;
import com.intellibird.cipollino.core.error.Status;
import com.intellibird.cipollino.core.error.Status.Severity;
import com.intellibird.cipollino.core.runtime.StartOptions;
import com.intellibird.cipollino.core.services.TransformationService;

public class Agent {

	private static final String OPTION_FILE = "file";

	protected final StartOptions options = new StartOptions();

	private static final Logger logger = LoggerFactory.getLogger(Agent.class);

	@Inject
	private TransformationService transformationService;

	private void start(String argsLine, Instrumentation instrumentation) {
		initDI();
		buildArgsOptions();
		Status status = parseArgs(argsLine);
		if (status.isSuccess()) {
			status = transformationService.start(instrumentation, options);
		}
		if (status.isError()) {
			logger.error(String.format("(%s) %s", status.getCode(), status.getMessage()));
			logger.error(ErrorCode.AgentWasnotStarted.formatMessage());
		}
	}

	private void initDI() {
		Injector injector = Guice.createInjector(new com.intellibird.cipollino.core.DIModule(),
				new com.intellibird.cipollino.logger.DIModule());
		injector.injectMembers(this);
	}

	protected Status parseArgs(String argsLine) {
		Status status = Status.createStatus(Severity.SUCCESS);
		String[] args = argsLine.split("&");
		CommandLineParser parser = new PosixParser();
		try {
			Options argsOptions = buildArgsOptions();
			CommandLine cl = parser.parse(argsOptions, args);
			if (cl.hasOption(OPTION_FILE)) {
				String fileName = cl.getOptionValue(OPTION_FILE);
				File file = new File(fileName);
				if (!file.exists()) {
					status.add(Status.createStatus(Severity.ERROR, ErrorCode.ControlFileNotFound.getCode(),
							ErrorCode.ControlFileNotFound.formatMessage(file.getAbsoluteFile())));
				} else {
					options.setControlFile(file);
				}
			}
		} catch (ParseException e) {
			status.add(Status.createStatus(ErrorCode.ArgumentsParsingError.getCode(), e));
		}
		return status;
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		main(agentArgs, inst);
	}

	public static void agentmain(String agentArgs, Instrumentation inst) {
		main(agentArgs, inst);
	}

	private static void main(String args, Instrumentation inst) {
		Agent agent = new Agent();
		agent.start(args, inst);
	}

	@SuppressWarnings("static-access")
	private Options buildArgsOptions() {
		Options argsOptions = new Options();
		argsOptions.addOption(OptionBuilder.hasArg().withLongOpt(OPTION_FILE).withDescription("Control File").create(
				'f'));
		return argsOptions;
	}
}
