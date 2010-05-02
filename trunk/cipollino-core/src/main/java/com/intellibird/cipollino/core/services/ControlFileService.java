package com.intellibird.cipollino.core.services;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.intellibird.cipollino.core.error.Status;
import com.intellibird.cipollino.core.error.Status.Severity;
import com.intellibird.cipollino.core.runtime.StartOptions;

@Singleton
public class ControlFileService extends TimerTask {

	private Logger logger = LoggerFactory.getLogger(ControlFileService.class);

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
			Status status = Status.createStatus(Severity.SUCCESS);
			logger.info("Loading control file");
			transformationService.reloadDirectives(status, controlFile);
		}
		lastModifiedTime = lastModified;
	}
}
