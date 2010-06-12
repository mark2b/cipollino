package org.cipollino.logger.actions;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.cipollino.core.model.ParameterDef;
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
			value = callState.getContext().get(key);
		}
		return null == value ? "null" : value.toString();
	}

	protected String formatMethodName(CallContext callState) {
		return callState.getMethodDef().getMethodName();
	}

	protected String formatParameters(CallContext callState) {
		StringBuilder builder = new StringBuilder();
		Object[] parameters = callState.getParameters();
		Collection<ParameterDef> parameterDefs = callState.getMethodDef()
				.getParameters().values();
		if (parameterDefs.size() > 0) {
			int i = 0;
			for (Iterator<ParameterDef> iterator = parameterDefs.iterator(); iterator
					.hasNext(); i++) {
				ParameterDef parameterDef = iterator.next();

				if (parameterDef.getIndex() < parameters.length) {
					appendParameter(builder, parameterDef.getName(),
							parameterDef.getIndex(),
							i == parameterDefs.size() - 1);
				}
			}
		} else {
			for (int i = 0; i < parameters.length; i++) {
				appendParameter(builder, "arg" + i, i,
						i == parameters.length - 1);
			}
		}
		return builder.toString();
	}

	private void appendParameter(StringBuilder builder, String name, int index,
			boolean last) {
		builder.append(name);
		builder.append(":");
		builder.append(formatParameter(index));
		if (!last) {
			builder.append(", ");
		}
	}

	abstract protected String formatParameter(Object object);

	protected Object formatResult(CallContext callState) {
		return formatParameter(callState.getResult());
	}
}
