package org.cipollino.core.services;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.cipollino.core.error.Status;
import org.cipollino.core.error.Status.Severity;
import org.cipollino.core.runtime.StartOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import static org.cipollino.core.error.ErrorCode.Info;

@Singleton
public class ControlFileService extends TimerTask {

	private Timer timer = new Timer(true);

	private long lastModifiedTime = 0L;

	private File controlFile;

	@Inject
	private TransformationService transformationService;

	public void start() {
		StartOptions options = transformationService.getOptions();
		controlFile = options.getControlFile();
		timer.scheduleAtFixedRate(this, 1000L, 1000L);
	}

	public void stop() {
		timer.cancel();
	}

	@Override
	public void run() {
		long lastModified = controlFile.lastModified();
		if (lastModifiedTime != 0 && lastModified > lastModifiedTime) {
			Status status = Status.createStatus();
			Info.print("Loading control file");
			transformationService.reloadConfiguration(status);
		}
		lastModifiedTime = lastModified;
	}
}
