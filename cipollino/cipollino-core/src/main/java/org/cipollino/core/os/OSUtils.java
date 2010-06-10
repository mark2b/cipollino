package org.cipollino.core.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class OSUtils {

	public static long BYTES_1K = 1024;

	public static long BYTES_1M = BYTES_1K * 1024;

	public static long BYTES_1G = BYTES_1M * 1024;

	File tempDir = new File(System.getProperty("java.io.tmpdir"));

	PrintStream stdout = null;

	PrintStream stderr = null;

	public ExecOutput exec(String command) throws Exception {
		exec2stream(command, null);
		return waitFor(Runtime.getRuntime().exec(command));
	}

	public ExecOutput exec(String[] cmdarray) throws Exception {
		exec2stream(cmdarray, null);
		return waitFor(Runtime.getRuntime().exec(cmdarray));
	}

	public ExecOutput exec(String[] cmdarray, String[] envp) throws Exception {
		exec2stream(cmdarray, envp);
		return waitFor(Runtime.getRuntime().exec(cmdarray, envp));
	}

	public ExecOutput exec(String[] cmdarray, String[] envp, File dir)
			throws Exception {
		exec2stream(cmdarray, envp);
		return waitFor(Runtime.getRuntime().exec(cmdarray, envp, dir));
	}

	public ExecOutput exec(String command, String[] envp) throws Exception {
		exec2stream(command, envp);
		return waitFor(Runtime.getRuntime().exec(command, envp));
	}

	public ExecOutput exec(String command, String[] envp, File dir)
			throws Exception {
		exec2stream(command, envp);
		return waitFor(Runtime.getRuntime().exec(command, envp, dir));
	}

	void exec2stream(Object command, String[] env) throws Exception {
		if (this.stdout != null) {
			if (command instanceof String) {
				this.stdout.write(((String) command).getBytes());
			} else if (command instanceof String[]) {
				String[] cmdarray = (String[]) command;
				for (int i = 0; i < cmdarray.length; i++) {
					this.stdout.write(cmdarray[i].getBytes());
					this.stdout.write(' ');
				}
			}
			this.stdout.write('\n');
			if (env != null && env.length > 0) {
				this.stdout.write(" : ENV ".getBytes());
				for (int i = 0; i < env.length; i++) {
					this.stdout.write(env[i].getBytes());
					this.stdout.write(' ');
				}
			}
		}
	}

	ExecOutput waitFor(Process p) throws Exception {
		ExecOutput out = new ExecOutput();
		out.process = p;
		StdOutReader stdOutReader = new StdOutReader(p.getInputStream(), p
				.getOutputStream(), out);
		StdErrReader stdErrReader = new StdErrReader(p.getErrorStream(), p
				.getOutputStream(), out);
		stdOutReader.start();
		stdErrReader.start();
		Thread.sleep(300);
		out.errorCode = p.waitFor();
		stdOutReader.waitFor(1000); // Wait for 1 sec after process has finished
		stdErrReader.waitFor(1000); // Wait for 1 sec after process has finished
		if (this.stderr != null) {
			for (String line : out.stderr) {
				this.stderr.println(line);
			}
			this.stderr.flush();
		}
		if (this.stdout != null) {
			this.stdout.write(("Exit Code = " + out.errorCode + "\n")
					.getBytes());
			for (String line : out.stdout) {
				this.stdout.println(line);
			}
			this.stdout.flush();
		}
		return out;
	}

	static public class ExecOutput {
		public Process process = null;

		public int errorCode = -1;

		public String[] stdout = new String[0];

		public String[] stderr = new String[0];
	}

	public File getTempDir() {
		return tempDir;
	}

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

	public void setStdOut(PrintStream os) {
		this.stdout = os;
	}

	public void setStdErr(PrintStream os) {
		this.stderr = os;
	}

	class StdOutReader extends ProcessOutputReader {
		final ExecOutput out;

		StdOutReader(InputStream is, OutputStream os, ExecOutput out) {
			super(is, os);
			this.out = out;
		}

		@Override
		public void run() {
			super.run();
			out.stdout = new String[lines.size()];
			lines.toArray(out.stdout);
		}
	}

	class StdErrReader extends ProcessOutputReader {
		final ExecOutput out;

		StdErrReader(InputStream is, OutputStream os, ExecOutput out) {
			super(is, os);
			this.out = out;
		}

		@Override
		public void run() {
			super.run();
			out.stderr = new String[lines.size()];
			lines.toArray(out.stderr);
		}
	}

	abstract class ProcessOutputReader extends Thread {
		final List<String> lines = new ArrayList<String>();

		final InputStream is;

		final OutputStream os;

		final Object sync = new Object();

		boolean stopped = false;

		ProcessOutputReader(InputStream is, OutputStream os) {
			setDaemon(true);
			this.is = is;
			this.os = os;
		}

		@Override
		public void run() {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null) {
					lines.add(line.trim());
					if (OSUtils.this.stdout != null) {
						OSUtils.this.stdout.println(line);
					}
				}
				synchronized (sync) {
					sync.notify();
					stopped = true;
				}
			} catch (IOException ex) {
			}
		}

		void waitFor() {
			waitFor(-1);
		}

		void waitFor(long timeout) {
			if (!stopped) {
				synchronized (sync) {
					try {
						if (timeout >= 0) {
							sync.wait(timeout);
						} else {
							sync.wait();
						}
					} catch (InterruptedException ex) {
					}
				}
			}
		}

	}

	public long getTotalPhysicalMemorySize() {
		return (Long) getPlatformMBeanAttribute(
				"java.lang:type=OperatingSystem", "TotalPhysicalMemorySize");
	}

	private Object getPlatformMBeanAttribute(String objectName,
			String attributeName) {
		try {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = new ObjectName(objectName);
			return server.getAttribute(name, attributeName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}