package org.cipollino.core.model;

import java.util.LinkedList;
import java.util.List;

public class Model {

	private List<TargetDef> targets = new LinkedList<TargetDef>();

	private ClassPathDef classPathDef = new ClassPathDef();

	public List<TargetDef> getTargets() {
		return targets;
	}

	public ClassPathDef getClassPathDef() {
		return classPathDef;
	}

	public void setClassPathDef(ClassPathDef classPathDef) {
		this.classPathDef = classPathDef;
	}

}