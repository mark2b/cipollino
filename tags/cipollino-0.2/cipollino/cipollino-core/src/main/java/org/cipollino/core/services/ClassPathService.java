package org.cipollino.core.services;

import static org.cipollino.core.error.ErrorCode.ClassPathNotFound;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javassist.ClassPool;
import javassist.NotFoundException;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.cipollino.core.model.ClassPathDef;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ClassPathService {

	private ClassPool parentClassPool;

	private ClassPool classPool;

	@Inject
	private ReplaceService replaceService;

	private ClassFileFilter classFileFilter = new ClassFileFilter();

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
			String transformedClassPath = replaceService
					.replaceBySystemProperties(cp);
			getClassPool().appendClassPath(transformedClassPath);
		} catch (NotFoundException e) {
			ClassPathNotFound.print(e.getMessage());
		}
	}

	private void updateClassPath2(String cp) {
		String path = replaceService.replaceBySystemProperties(cp);
		if (path.contains(File.pathSeparator)) {
			String[] pathes = path.split(File.pathSeparator);
			for (String pathItem : pathes) {
				appendPath(pathItem);
			}
		} else {
			appendPath(path);
		}
	}

	private void appendPath(String path) {
		path = StringUtils.stripEnd(path, "/");
		File file = new File(path);

		if (file.exists()) {
			if (file.isDirectory()) {
				if (isClassesDir(file)) {
					appendClassPath(file);
				} else {
				}
			} else if (file.isFile()) {
			}
		} else {
			ClassPathNotFound.print(file.getAbsolutePath());
		}
		// try {
		// getClassPool().appendClassPath(transformedClassPath);
		// } catch (NotFoundException e) {
		// ClassPathNotFound.print(e.getMessage());
		// }
	}

	private void appendClassPath(File file) {
		try {
			getClassPool().appendClassPath(file.getAbsolutePath() + "/");
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

	private boolean isClassesDir(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles(classFileFilter);
			if (files.length > 0) {
				return true;
			} else {
				for (File file : files) {
					boolean exists = isClassesDir(file);
					if (exists) {
						return true;
					}
				}
			}
		}
		return false;
	}

	static class ClassFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return pathname.getAbsolutePath().endsWith(".class");
		}
	}

	static class ClassPathDirectoryWalker extends DirectoryWalker {

		public File resolveClassPath() throws Exception {
			List<File> results = new ArrayList<File>();
			File startDirectory = File.createTempFile("cipollino-", ".tmp");
			walk(startDirectory, results);
			return startDirectory;
		}

		@Override
		protected boolean handleDirectory(File directory, int depth,
				Collection results) throws IOException {
			return super.handleDirectory(directory, depth, results);
		}

		@Override
		protected void handleFile(File file, int depth, Collection results)
				throws IOException {
			//FileUtils.copyFile(srcFile, destFile)
			// TODO Auto-generated method stub
			super.handleFile(file, depth, results);
		}
	}
}
