package org.cipollino.core.model;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TargetDef {

	private final Map<String, MethodDef> methods = new LinkedHashMap<String, MethodDef>();

	private final List<ActionDef> actions = new LinkedList<ActionDef>();

	public Map<String, MethodDef> getMethods() {
		return methods;
	}

	public List<ActionDef> getActions() {
		return actions;
	}
}
