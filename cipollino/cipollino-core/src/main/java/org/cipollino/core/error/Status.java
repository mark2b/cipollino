//package org.cipollino.core.error;
//
//import java.util.Arrays;
//import java.util.Date;
//
//public class Status {
//
//	public enum Severity {
//		SUCCESS, WARNING, ERROR;
//	}
//
//	private Severity severity;
//
//	private Enum<?> errorMessage;
//
//	private ErrorMessageMetadata metadata;
//
//	private long timestamp;
//
//	transient private Throwable exception;
//
//	private Status[] children = new Status[0];
//
//	@SuppressWarnings("unused")
//	// For JSON deserialization only
//	private Status() {
//	}
//
//	// public Status(Severity severity) {
//	// this(severity, null, null);
//	// }
//
//	public Status(Enum<?> errorMessage) {
//		this(System.currentTimeMillis(), errorMessage, null);
//	}
//
//	public Status(Enum<?> errorMessage, Throwable exception) {
//		this(System.currentTimeMillis(), errorMessage, exception);
//	}
//
//	public Status(long timestamp, Enum<?> errorMessage, Throwable exception) {
//		this.errorMessage = errorMessage;
//		this.exception = exception;
//		this.timestamp = timestamp;
//		this.severity = toSeverity(ErrorUtils.getMetadata(errorMessage).severity);
//		metadata = ErrorUtils.getMetadata(errorMessage);
//	}
//
//	public Throwable getException() {
//		return this.exception;
//	}
//
//	public Severity getSeverity() {
//		return severity == null ? Severity.SUCCESS : severity;
//	}
//
//	public Date getTime() {
//		return new Date(timestamp);
//	}
//
//	private Status[] getChildren() {
//		return children;
//	}
//
//	public boolean isSuccess() {
//		return getSeverity().equals(Severity.SUCCESS);
//	}
//
//	public boolean isWarning() {
//		return getSeverity().equals(Severity.WARNING);
//	}
//
//	public boolean isError() {
//		return getSeverity().equals(Severity.ERROR);
//	}
//
//	public void add(Status status) {
//		if (!isMultiStatus()) {
//			addStatus(new Status(timestamp, errorMessage, exception));
//		}
//		if (status.isMultiStatus()) {
//			for (Status childStatus : status.getChildren()) {
//				addStatus(childStatus);
//			}
//		} else {
//			addStatus(status);
//		}
//		Status worstStatus = getTopStatusForWorstSeverity();
//		this.severity = worstStatus.severity;
//		this.errorMessage = worstStatus.errorMessage;
//		this.exception = worstStatus.exception;
//		this.metadata = worstStatus.metadata;
//	}
//
//	private boolean isMultiStatus() {
//		return children.length > 0;
//	}
//
//	private void addStatus(Status status) {
//		children = Arrays.copyOf(children, children.length + 1);
//		children[children.length - 1] = status;
//	}
//
//	private Severity getWorstSeverity() {
//		Severity severity = null;
//		if (isMultiStatus()) {
//			for (Status status : getChildren()) {
//				if (severity == null || status.severity.compareTo(severity) > 0) {
//					severity = status.severity;
//				}
//			}
//		}
//		return severity == null ? Severity.SUCCESS : severity;
//	}
//
//	private Status getTopStatusForWorstSeverity() {
//		Severity severity = getWorstSeverity();
//		Status[] children = getChildren();
//		for (int i = children.length - 1; i >= 0; i--) {
//			if (children[i].getSeverity().equals(severity)) {
//				return children[i];
//			}
//		}
//		return new Status(ErrorCode.Success);
//	}
//
//	private Severity toSeverity(org.cipollino.core.logger.Severity severity) {
//		switch (severity) {
//		case ERROR:
//			return Severity.ERROR;
//		case WARNING:
//			return Severity.WARNING;
//		default:
//			return Severity.SUCCESS;
//		}
//	}
//
//	public static Status createStatus(Enum<?> errorMessage, Exception e) {
//		return new Status(errorMessage, e);
//	}
//
//	public static Status createStatus(Enum<?> errorMessage) {
//		return new Status(errorMessage);
//	}
//
//	public static Status createStatus() {
//		return new Status(ErrorCode.Success);
//	}
//
//	public String getCode() {
//		return metadata.code;
//	}
//
//	public String getMessage() {
//		return metadata.message;
//	}
//
//	public ErrorMessage getErrorMessage() {
//		return (ErrorMessage) errorMessage;
//	}
//}
