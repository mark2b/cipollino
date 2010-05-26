package org.cipollino.itests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeClass;

public abstract class AbstractTest {

	private final List<InputStream> inputStreams = new ArrayList<InputStream>();

	protected File getJavaHome() {
		return new File(System.getProperty("java.home"));
	}

	protected File getJavaBin() {
		return new File(getJavaHome(), "bin");
	}

	@SuppressWarnings("unchecked")
	protected Map<String, String> getProcesses() throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		ProcessBuilder builder = new ProcessBuilder(new File(getJavaBin(),
				"jps").getAbsolutePath());
		builder.command().add("-l");
		Process process = builder.start();
		List<String> lines = IOUtils.readLines(process.getInputStream());
		for (String line : lines) {
			String[] tokens = line.split(" ");
			if (tokens.length == 2) {
				map.put(tokens[1], tokens[0]);
			}
		}
		return map;
	}

	protected Process startProcess(String... commands) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(new File(getJavaBin(),
				"java").getAbsolutePath());
		for (String command : commands) {
			builder.command().add(command);
		}
		builder.redirectErrorStream(true);
		Process process = builder.start();
		inputStreams.add(process.getErrorStream());
		inputStreams.add(process.getInputStream());
		return process;
	}

	private void startOutputPrinter() {
		new Thread() {

			@Override
			public void run() {
				while (true) {
					InputStream[] streams = inputStreams
							.toArray(new InputStream[inputStreams.size()]);
					for (InputStream stream : streams) {
						try {
							IOUtils.copy(stream, System.out);
						} catch (IOException e) {
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}.start();
	}

	@BeforeClass
	public void beforeClass() {
		startOutputPrinter();
	}
}
