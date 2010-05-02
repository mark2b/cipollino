package com.intellibird.cipollino.logger.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.intellibird.cipollino.core.runtime.CallState;

public abstract class AbstractFormatter implements Formatter {

	private Map<String, String> placeHolders = null;

	private static final Pattern pattern = Pattern.compile("(\\$\\{[a-z|A-Z][a-z|A-Z|0-9|\\.\\-]*\\})");

	@Override
	public String format(String format, CallState callState) {
		StringBuilder builder = new StringBuilder(format);
		Map<String, String> placeHolders = getPlaceHolders(format);
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

	protected Map<String, String> getPlaceHolders(String format) {
		if (placeHolders == null) {
			placeHolders = new HashMap<String, String>();
			Matcher m = pattern.matcher(format);
			while (m.find()) {
				String placeHolder = m.group(1);
				String name = StringUtils.substringBetween(placeHolder, "${", "}");
				placeHolders.put(name, placeHolder);
			}
		}
		return placeHolders;
	}

	protected String getValueFor(String key, CallState callState) {
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

	protected String formatMethodName(CallState callState) {
		return callState.getMethodDef().getMethodName();
	}

	protected String formatParameters(CallState callState) {
		StringBuilder builder = new StringBuilder();
		Object[] parameters = callState.getParameters();

		for (int i = 0; i < parameters.length; i++) {
			builder.append("arg" + i + ":" + formatParameter(parameters[i]) + ", ");
		}
		return builder.toString();
	}

	abstract protected String formatParameter(Object object);

	protected Object formatResult(CallState callState) {
		return formatParameter(callState.getResult());
	}

}
