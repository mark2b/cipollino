package org.cipollino.logger.actions;

import java.util.Map;
import java.util.Map.Entry;

import org.cipollino.core.runtime.CallContext;
import org.cipollino.core.services.ReplaceService;
import org.cipollino.core.services.ReplaceService.ParseInfo;

import com.google.inject.Inject;

public abstract class AbstractFormatter implements Formatter {

	private ParseInfo parseInfo = null;

	@Inject
	private ReplaceService replaceService;

	@Override
	public String format(String format, CallContext callState) {
		StringBuilder builder = new StringBuilder(format);
		if (parseInfo == null) {
			parseInfo = replaceService.parse(format);
		}
		Map<String, String> placeHolders = parseInfo.getPlaceHolders();
		for (Entry<String, String> entry : placeHolders.entrySet()) {
			String placeHolder = entry.getValue();
			int fromIndex = 0;
			int index;
			while ((index = builder.indexOf(placeHolder, fromIndex)) >= 0) {
				int endIndex = index + entry.getValue().length();
				String value = getValueFor(entry.getKey(), callState);
				builder.replace(index, endIndex, value);
				fromIndex = endIndex;
			}
		}
		return builder.toString();
	}

	protected String getValueFor(String key, CallContext callState) {
		Object value = null;
		if ("method".equals(key)) {
			value = formatMethodName(callState);
		} else if ("parameters".equals(key)) {
			value = formatParameters(callState);
		} else if ("result".equals(key)) {
			value = formatResult(callState);
		} else {
			value = callState.getStateMap().get(key);
		}
		return null == value ? "null" : value.toString();
	}

	protected String formatMethodName(CallContext callState) {
		return callState.getMethodDef().getMethodName();
	}

	protected String formatParameters(CallContext callState) {
		StringBuilder builder = new StringBuilder();
		Object[] parameters = callState.getParameters();

		for (int i = 0; i < parameters.length; i++) {
			builder.append("arg" + i + ":" + formatParameter(parameters[i]) + ", ");
		}
		return builder.toString();
	}

	abstract protected String formatParameter(Object object);

	protected Object formatResult(CallContext callState) {
		return formatParameter(callState.getResult());
	}
}
