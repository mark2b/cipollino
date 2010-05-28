package org.cipollino.agent;

import static org.cipollino.agent.error.ErrorCode.AgentWasConnected;
import static org.cipollino.agent.error.ErrorCode.AgentWasnotConnected;
import static org.cipollino.agent.error.ErrorCode.ArgumentsParsingError;
import static org.cipollino.agent.error.ErrorCode.ControlFileMissing;
import static org.cipollino.agent.error.ErrorCode.ControlFileNotFound;
import static org.cipollino.agent.error.ErrorCode.PidFileNotFound;
import static org.cipollino.agent.error.ErrorCode.PidMissing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
import org.cipollino.core.error.ErrorCode;

import com.sun.tools.attach.VirtualMachine;

@SuppressWarnings("restriction")
public class Main {
	private static final String CIPOLLINO_LOG_FILE = "cipollino.log.file";

	private static final String OPTION_FILE = "file";

	private static final String OPTION_PID = "pid";

	private static final String OPTION_PID_FILE = "pid-file";

	private final StartOptions options = new StartOptions();

	public static void main(String[] args) {
		System.setProperty(CIPOLLINO_LOG_FILE, System.getProperty(
				CIPOLLINO_LOG_FILE, "cipollino.log"));

		Main main = new Main();
		main.start(args);
	}

	private void start(String[] args) {
		try {
			parseArgs(args);
			buildArgsOptions();
			connectToVM();
			AgentWasConnected.print();
			System.exit(0);
		} catch (RuntimeException e) {
			AgentWasnotConnected.print(e.getMessage());
		} catch (Exception e) {
			AgentWasnotConnected.print(e.getMessage(), e);
		}
		System.exit(-1);
	}

	private void connectToVM() {
		try {
			URLClassLoader classLoader = (URLClassLoader) ClassLoader
					.getSystemClassLoader();
			URL[] urls = classLoader.getURLs();
			if (urls.length == 0) {
				throw new RuntimeException();
			}
			String jarName = new File(urls[0].getFile()).getAbsolutePath();
			VirtualMachine machine = VirtualMachine.attach(options.getPid());
			machine.loadAgent(jarName, String.format("--file=%s", options
					.getControlFile().getAbsolutePath()));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	private void parseArgs(String[] args) {
		CommandLineParser parser = new PosixParser();
		try {
			Options argsOptions = buildArgsOptions();
			CommandLine cl = parser.parse(argsOptions, args);
			if (cl.hasOption(OPTION_FILE)) {
				String fileName = cl.getOptionValue(OPTION_FILE);
				File file = new File(fileName);
				if (!file.exists()) {
					throw new RuntimeException(ControlFileNotFound.format(file
							.getAbsolutePath()));
				} else {
					options.setControlFile(file);
				}
			} else {
				throw new RuntimeException(ControlFileMissing.format());
			}
			if (cl.hasOption(OPTION_PID) || cl.hasOption(OPTION_PID_FILE)) {
				if (cl.hasOption(OPTION_PID)) {
					options.setPid(cl.getOptionValue(OPTION_PID));
				} else {
					String fileName = cl.getOptionValue(OPTION_PID_FILE);
					File file = new File(fileName);
					if (!file.exists()) {
						throw new RuntimeException(PidFileNotFound.format(file
								.getAbsolutePath()));
					} else {
						try {
							FileReader reader = new FileReader(file);
							String pid = IOUtils.toString(reader).trim();
							options.setPid(pid);
							IOUtils.closeQuietly(reader);
						} catch (FileNotFoundException e) {
							throw new RuntimeException(PidFileNotFound
									.format(file.getAbsolutePath()));
						} catch (IOException e) {
							throw new RuntimeException(ErrorCode.InternalError
									.format(e.getMessage(), e));
						}
					}
				}
			} else {
				throw new RuntimeException(PidMissing.format());
			}
		} catch (ParseException e) {
			throw new RuntimeException(ArgumentsParsingError.format(e));
		}
	}

	@SuppressWarnings("static-access")
	private Options buildArgsOptions() {
		Options argsOptions = new Options();
		argsOptions.addOption(
				OptionBuilder.hasArg().withLongOpt(OPTION_FILE)
						.withDescription("Control File").create()).addOption(
				OptionBuilder.hasArg().withLongOpt(OPTION_PID).withDescription(
						"Process ID").create()).addOption(
				OptionBuilder.hasArg().withLongOpt(OPTION_PID_FILE)
						.withDescription("Process ID file").create());
		return argsOptions;
	}

	static class StartOptions {

		private File controlFile;

		private String pid;

		public File getControlFile() {
			return controlFile;
		}

		public void setControlFile(File controlFile) {
			this.controlFile = controlFile;
		}

		public String getPid() {
			return pid;
		}

		public void setPid(String pid) {
			this.pid = pid;
		}
	}
}
