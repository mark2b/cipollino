package com.google.cipollino.core.error;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Status {

	public enum Severity {
		SUCCESS, INFO, WARNING, ERROR;
	}

	private Severity severity;

	private String code;

	private String message;

	private long timestamp;

	transient private Throwable exception;

	private Status[] children = new Status[0];

	@SuppressWarnings("unused")
	// For JSON deserialization only
	private Status() {
	}

	public Status(Severity severity) {
		this(severity, null, null);
	}

	public Status(Severity severity, String code, String message) {
		this(severity, code, message, null);
	}

	public Status(Severity severity, String code, String message, Throwable exception) {
		this(System.currentTimeMillis(), severity, code, message, exception);
	}

	private Status(long timestamp, Severity severity, String code, String message, Throwable exception) {
		this.severity = severity;
		this.code = code;
		this.message = message == null ? severity.toString() : message;
		this.exception = exception;
		this.timestamp = timestamp;
	}

	public String getCode() {
		return this.code;
	}

	public String getMessage() {
		return this.message;
	}

	public Throwable getException() {
		return this.exception;
	}

	public Severity getSeverity() {
		return severity == null ? Severity.SUCCESS : severity;
	}

	public Date getTime() {
		return new Date(timestamp);
	}

	private Status[] getChildren() {
		return children;
	}

	public boolean isSuccess() {
		return getSeverity().equals(Severity.SUCCESS);
	}

	public boolean isWarning() {
		return getSeverity().equals(Severity.WARNING);
	}

	public boolean isError() {
		return getSeverity().equals(Severity.ERROR);
	}

	public static Status createStatus(Severity severity) {
		return new Status(severity);
	}

	public static Status createStatus(Severity severity, String code, String message) {
		return new Status(severity, code, message);
	}

	public static Status createStatus(Severity severity, Throwable e) {
		return new Status(severity, e.getClass().getName(), e.getMessage(), e);
	}

	public static Status createStatus(Severity severity, String code, Throwable e) {
		return new Status(severity, code, e.getMessage(), e);
	}

	public static Status createStatus(String code, Throwable e) {
		return createStatus(Severity.ERROR, code, e);
	}

	public static Status createStatus(Throwable e) {
		return createStatus(Severity.ERROR, e);
	}

	public void add(Status status) {
		if (!isMultiStatus()) {
			addStatus(new Status(timestamp, severity, code, message, exception));
		}
		if (status.isMultiStatus()) {
			for (Status childStatus : status.getChildren()) {
				addStatus(childStatus);
			}
		} else {
			addStatus(status);
		}
		Status worstStatus = getTopStatusForWorstSeverity();
		this.severity = worstStatus.severity;
		this.code = worstStatus.code;
		this.message = worstStatus.message;
		this.exception = worstStatus.exception;
	}

	private boolean isMultiStatus() {
		return children.length > 0;
	}

	private void addStatus(Status status) {
		children = Arrays.copyOf(children, children.length + 1);
		children[children.length - 1] = status;
	}

	private Severity getWorstSeverity() {
		Severity severity = null;
		if (isMultiStatus()) {
			for (Status status : getChildren()) {
				if (severity == null || status.severity.compareTo(severity) > 0) {
					severity = status.severity;
				}
			}
		}
		return severity == null ? Severity.SUCCESS : severity;
	}

	private Status getTopStatusForWorstSeverity() {
		Severity severity = getWorstSeverity();
		Status[] children = getChildren();
		for (int i = children.length - 1; i >= 0; i--) {
			if (children[i].getSeverity().equals(severity)) {
				return children[i];
			}
		}
		return new Status(severity, "", "");
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("severity", getSeverity().toString());
		if (code != null) {
			map.put("code", code);
		}
		if (!Severity.SUCCESS.equals(severity)) {
			map.put("message", getMessage());
		}
		map.put("timestamp", getTime());
		for (Status status : getChildren()) {
			map.put("status", status.toMap());
		}
		return map;
	}
}
