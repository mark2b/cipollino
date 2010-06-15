package org.cipollino.core.runtime;

import java.io.File;

public class StartOptions {

	private boolean attached;

	private File controlFile;

	public boolean isAttached() {
		return attached;
	}

	public void setAttached(boolean attached) {
		this.attached = attached;
	}

	public File getControlFile() {
		return controlFile;
	}

	public void setControlFile(File controlFile) {
		this.controlFile = controlFile;
	}
}
