package org.cipollino.core.services;

import static org.cipollino.core.error.ErrorCode.ClassPathNotFound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cipollino.core.model.ClassPathDef;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.NotFoundException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ClassPathService {

	@Inject
	private ClassPool classPool;

	@Inject
	private ReplaceService replaceService;

	private Map<String, ClassPath> classPathMap = new HashMap<String, ClassPath>();

	public void updateClassPool(ClassPathDef classPathDef) {
		Map<String, ClassPath> localClassPathMap = new HashMap<String, ClassPath>();
		for (String cp : classPathDef.getClasses()) {
			updateClassPath(cp, localClassPathMap);
		}
		for (String cp : classPathDef.getDir()) {
			updateClassPath(cp + "/*", localClassPathMap);
		}
		for (String cp : classPathDef.getJar()) {
			updateClassPath(cp, localClassPathMap);
		}
		for (String cp : classPathDef.getPath()) {
			updateClassPath(cp, localClassPathMap);
		}
		for (Entry<String, ClassPath> entry : classPathMap.entrySet()) {
			if (!localClassPathMap.containsKey(entry.getKey())) {
				classPool.removeClassPath(entry.getValue());
			}
		}
		classPathMap = localClassPathMap;
	}

	private void updateClassPath(String cp, Map<String, ClassPath> localClassPathMap) {
		if (!classPathMap.containsKey(cp)) {
			try {
				String transformedClassPath = replaceService.replaceBySystemProperties(cp);
				ClassPath classPath = classPool.appendClassPath(transformedClassPath);
				localClassPathMap.put(cp, classPath);
			} catch (NotFoundException e) {
				ClassPathNotFound.print(e.getMessage());
			}
		}
	}

	public ClassPath[] createClassPath(ClassPathDef classPathDef) {
		List<ClassPath> classPathsList = new ArrayList<ClassPath>();
		for (String cp : classPathDef.getClasses()) {
			try {
				ClassPath classPath = classPool.appendClassPath(cp);
				classPathsList.add(classPath);
			} catch (NotFoundException e) {
				ClassPathNotFound.print(e.getMessage());
			}
		}
		return classPathsList.toArray(new ClassPath[classPathsList.size()]);
	}
}
