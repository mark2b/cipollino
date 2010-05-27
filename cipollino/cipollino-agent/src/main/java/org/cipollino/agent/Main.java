package org.cipollino.agent;

import static org.cipollino.agent.error.ErrorCode.AgentWasStarted;
import static org.cipollino.agent.error.ErrorCode.AgentWasnotStarted;
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
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
import org.cipollino.core.error.Status;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class Main {
	private static final String OPTION_FILE = "file";

	private static final String OPTION_PID = "pid";

	private static final String OPTION_PID_FILE = "pid-file";

	private final StartOptions options = new StartOptions();

	public static void main(String[] args) {

		Main main = new Main();
		main.start(args);
	}

	private void start(String[] args) {
		buildArgsOptions();
		Status status = parseArgs(args);
		if (status.isSuccess()) {
			connectToVM(status);
		}
		if (status.isError()) {
			status.getErrorMessage().print();
			AgentWasnotStarted.print();
			System.exit(-1);
		} else {
//			AgentWasStarted.print();
			System.exit(0);
		}
	}

	private void connectToVM(Status status) {
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
		} catch (AttachNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AgentLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AgentInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Status parseArgs(String[] args) {
		Status status = Status.createStatus();
		CommandLineParser parser = new PosixParser();
		try {
			Options argsOptions = buildArgsOptions();
			CommandLine cl = parser.parse(argsOptions, args);
			if (cl.hasOption(OPTION_FILE)) {
				String fileName = cl.getOptionValue(OPTION_FILE);
				File file = new File(fileName);
				if (!file.exists()) {
					status.add(Status.createStatus(ControlFileNotFound));
				} else {
					options.setControlFile(file);
				}
			} else {
				status.add(Status.createStatus(ControlFileMissing));
			}
			if (cl.hasOption(OPTION_PID) || cl.hasOption(OPTION_PID_FILE)) {
				if (cl.hasOption(OPTION_PID)) {
					options.setPid(cl.getOptionValue(OPTION_PID));
				} else {
					String fileName = cl.getOptionValue(OPTION_PID_FILE);
					File file = new File(fileName);
					if (!file.exists()) {
						status.add(Status.createStatus(PidFileNotFound));
					} else {
						FileReader reader = new FileReader(file);
						String pid = IOUtils.toString(reader).trim();
						options.setPid(pid);
						IOUtils.closeQuietly(reader);
					}
				}
			} else {
				status.add(Status.createStatus(PidMissing));
			}
		} catch (ParseException e) {
			status.add(Status.createStatus(ArgumentsParsingError, e));
		} catch (FileNotFoundException e) {
			PidFileNotFound.print(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
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

		private File jar;

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
