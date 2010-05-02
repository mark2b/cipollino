package com.intellibird.cipollino.logger.actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONFormatter extends AbstractFormatter {

	private Gson gson;

	public JSONFormatter() {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		gson = builder.create();
	}

	protected String formatParameter(Object object) {
		return gson.toJson(object);
	}
}
