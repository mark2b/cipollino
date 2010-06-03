package org.cipollino.core.services;

import static org.cipollino.core.error.ErrorCode.ClassPathNotFound;
import javassist.ClassPool;
import javassist.NotFoundException;

import org.cipollino.core.model.ClassPathDef;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ClassPathService {

	private ClassPool parentClassPool;

	private ClassPool classPool;

	@Inject
	private ReplaceService replaceService;

	public synchronized void updateClassPool(ClassPathDef classPathDef) {
		// Drop old class poll
		classPool = null;
		for (String cp : classPathDef.getClasses()) {
			if (!cp.endsWith("/")) {
				cp += "/";
			}
			updateClassPath(cp);
		}
		for (String cp : classPathDef.getDir()) {
			if (!cp.endsWith("/*")) {
				if (cp.endsWith("/")) {
					cp += "*";
				} else {
					cp += "/*";
				}
			}
			updateClassPath(cp);
		}
		for (String cp : classPathDef.getJar()) {
			updateClassPath(cp);
		}
		for (String cp : classPathDef.getPath()) {
			updateClassPath(cp);
		}
	}

	private void updateClassPath(String cp) {
		try {
			String transformedClassPath = replaceService.replaceBySystemProperties(cp);
			getClassPool().appendClassPath(transformedClassPath);
		} catch (NotFoundException e) {
			ClassPathNotFound.print(e.getMessage());
		}
	}

	public synchronized ClassPool getClassPool() {
		if (classPool == null) {
			if (parentClassPool == null) {
				parentClassPool = new ClassPool();
				parentClassPool.appendSystemPath();
			}
			classPool = new ClassPool(parentClassPool);
		}
		return classPool;
	}
}
