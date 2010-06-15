package org.cipollino.core.model;

import java.util.LinkedList;
import java.util.List;

public class ClassPathDef {

	private List<String> classes = new LinkedList<String>();

	private List<String> jar = new LinkedList<String>();

	private List<String> dir = new LinkedList<String>();

	private List<String> path = new LinkedList<String>();

	public List<String> getClasses() {
		return classes;
	}

	public List<String> getJar() {
		return jar;
	}

	public List<String> getDir() {
		return dir;
	}

	public List<String> getPath() {
		return path;
	}

}
