package org.cipollino.core.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Singleton;

@Singleton
public class ReplaceService {

	public static class ParseInfo {

		private Map<String, String> placeHolders = new HashMap<String, String>();

		public Map<String, String> getPlaceHolders() {
			return placeHolders;
		}
	}

	private static final Pattern pattern = Pattern.compile("(\\$\\{[a-z|A-Z][a-z|A-Z|0-9|\\.\\-]*\\})");

	public String replaceBySystemProperties(String input) {
		return replaceByProperties(input, System.getProperties());
	}

	public ParseInfo parse(String input) {
		ParseInfo info = new ParseInfo();
		Map<String, String> placeHolders = new HashMap<String, String>();
		Matcher m = pattern.matcher(input);
		while (m.find()) {
			String placeHolder = m.group(1);
			String name = StringUtils.substringBetween(placeHolder, "${", "}");
			placeHolders.put(name, placeHolder);
		}
		info.placeHolders = placeHolders;
		return info;
	}

	public String replaceByProperties(String input, Properties properties) {
		ParseInfo info = parse(input);
		return replaceByProperties(input, properties, info);
	}

	public String replaceByProperties(String input, Properties properties, ParseInfo info) {
		StringBuilder builder = new StringBuilder(input);
		Map<String, String> placeHolders = info.placeHolders;
		for (Entry<String, String> entry : placeHolders.entrySet()) {
			String placeHolder = entry.getValue();
			int fromIndex = 0;
			int index;
			while ((index = builder.indexOf(placeHolder, fromIndex)) >= 0) {
				int endIndex = index + entry.getValue().length();
				String value = getValueFor(entry.getKey(), properties);
				if (value != null) {
					builder.replace(index, endIndex, value);
				}
				fromIndex = endIndex;
			}
		}
		return builder.toString();
	}

	private String getValueFor(String key, Properties properties) {
		return properties.getProperty(key);
	}
}
