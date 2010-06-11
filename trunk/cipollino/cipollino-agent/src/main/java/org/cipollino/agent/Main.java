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
import org.cipollino.core.error.ErrorException;
import org.cipollino.core.os.OSFamily;
import org.cipollino.core.os.OSType;

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
		int exitCode = main.start(args);
		System.exit(exitCode);
	}

	private int start(String[] args) {
		try {
			parseArgs(args);
			buildArgsOptions();
			connectToVM();
			AgentWasConnected.print();
			return 0;
		} catch (final ErrorException e) {
			e.getErrorMessage().print(e.getArgs());
			return 201;
		} catch (final Exception e) {
			AgentWasnotConnected.print(e.getMessage(), e);
			return 202;
		}
	}

	@SuppressWarnings("unchecked")
	private void connectToVM() {
		try {
			URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader
					.getSystemClassLoader();
			URL[] systemUrls = systemClassLoader.getURLs();
			if (systemUrls.length == 0) {
				throw new ErrorException(ErrorCode.InternalError,
						"Invalid Agent Jar");
			}
			String jarName = new File(systemUrls[0].getFile())
					.getAbsolutePath();

			ClassLoader classLoader = systemClassLoader;
			OSType osType = OSType.getCurrent();
			System.out.println("Main.connectToVM()");
			if (!osType.getFamily().equals(OSFamily.MAC)) {
				System.out.println("Main.connectToVM(1)");
				File toolsJarFile = getToolsJarFile();
				if (!toolsJarFile.exists()) {
					System.out.println("Main.connectToVM(11)");
					throw new ErrorException(
							org.cipollino.agent.error.ErrorCode.MissingToolsJar,
							getJavaHome().getAbsolutePath());
				}
				System.out.println("Main.connectToVM(111) " + toolsJarFile);
				classLoader = new URLClassLoader(new URL[] { toolsJarFile
						.toURI().toURL() });
			}
			System.out.println("Main.connectToVM(2)");
			Class virtualMachineClass = classLoader
					.loadClass("com.sun.tools.attach.VirtualMachine");

			Object machine = virtualMachineClass.getMethod("attach",
					String.class).invoke(null, options.getPid());
			virtualMachineClass.getMethod("loadAgent", String.class,
					String.class).invoke(
					machine,
					jarName,
					String.format("--file=%s", options.getControlFile()
							.getAbsolutePath()));
		} catch (Exception e) {
			throw new ErrorException(AgentWasnotConnected, e.getMessage());
		}

	}

	private File getToolsJarFile() {
		File javaHome = getJavaHome();
		File toolsJarFile = new File(javaHome, "lib/tools.jar");
		return toolsJarFile;
	}

	private File getJavaHome() {
		File javaHome = new File(System.getProperty("java.home"));
		return javaHome;
	}

	private void parseArgs(String[] args) {
		final CommandLineParser parser = new PosixParser();
		try {
			final Options argsOptions = buildArgsOptions();
			final CommandLine cl = parser.parse(argsOptions, args);
			if (cl.hasOption(OPTION_FILE)) {
				final String fileName = cl.getOptionValue(OPTION_FILE);
				final File file = new File(fileName);
				if (!file.exists()) {
					throw new ErrorException(ControlFileNotFound, file
							.getAbsolutePath());
				} else {
					options.setControlFile(file);
				}
			} else {
				throw new ErrorException(ControlFileMissing);
			}
			if (cl.hasOption(OPTION_PID) || cl.hasOption(OPTION_PID_FILE)) {
				if (cl.hasOption(OPTION_PID)) {
					options.setPid(cl.getOptionValue(OPTION_PID));
				} else {
					final String fileName = cl.getOptionValue(OPTION_PID_FILE);
					final File file = new File(fileName);
					if (!file.exists()) {
						throw new ErrorException(PidFileNotFound, file
								.getAbsolutePath());
					} else {
						try {
							final FileReader reader = new FileReader(file);
							final String pid = IOUtils.toString(reader).trim();
							options.setPid(pid);
							IOUtils.closeQuietly(reader);
						} catch (final FileNotFoundException e) {
							throw new ErrorException(PidFileNotFound, file
									.getAbsolutePath());
						} catch (final IOException e) {
							throw new ErrorException(ErrorCode.InternalError,
									e, e.getMessage());
						}
					}
				}
			} else {
				throw new ErrorException(PidMissing);
			}
		} catch (final ParseException e) {
			throw new ErrorException(ArgumentsParsingError, e.getMessage());
		}
	}

	@SuppressWarnings("static-access")
	private Options buildArgsOptions() {
		final Options argsOptions = new Options();
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
