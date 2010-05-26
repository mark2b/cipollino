package org.cipollino.core.services;

import static org.cipollino.core.error.ErrorCode.ClassCanNotBeTransformed;
import static org.cipollino.core.error.ErrorCode.ClassNotFound;
import static org.cipollino.core.error.ErrorCode.CompilationFailure;
import static org.cipollino.core.error.ErrorCode.ControlFileMissing;
import static org.cipollino.core.error.ErrorCode.ControlFileNotFound;
import static org.cipollino.core.error.ErrorCode.RuntimeClassNotFound;
import static org.cipollino.core.error.ErrorCode.RuntimeNonUniqueClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.cipollino.core.error.Status;
import org.cipollino.core.inst.ClassTransformer;
import org.cipollino.core.model.ActionDef;
import org.cipollino.core.model.Agent;
import org.cipollino.core.model.MethodDef;
import org.cipollino.core.model.ScriptDef;
import org.cipollino.core.model.TargetDef;
import org.cipollino.core.parsers.MethodParser;
import org.cipollino.core.runtime.AbstractScript;
import org.cipollino.core.runtime.CallState;
import org.cipollino.core.runtime.ClassData;
import org.cipollino.core.runtime.Runtime;
import org.cipollino.core.runtime.Script;
import org.cipollino.core.runtime.StartOptions;
import org.cipollino.core.schema.AgentType;
import org.cipollino.core.xml.AbstractX2JModelFactory;
import org.cipollino.core.xml.ModelSerializer;
import org.cipollino.core.xml.X2JModelFactoryFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TransformationService {

	private StartOptions options;

	private Instrumentation instrumentation;

	@Inject
	private ModelSerializer modelSerializer;

	@Inject
	private X2JModelFactoryFactory modelFactoryFactory;

	@Inject
	private Runtime runtime;

	@Inject
	private MethodParser methodParser;

	@Inject
	private ClassTransformer classTransformer;

	@Inject
	private ControlFileService controlFileService;

	@Inject
	private ClassPathService classPathService;

	@Inject
	private ClassPool classPool;

	public Status start(final Instrumentation instrumentation,
			final StartOptions options) {
		this.options = options;
		final Status status = Status.createStatus();
		this.instrumentation = instrumentation;
		if (options.getControlFile() != null) {
			if (loadControlFile(status, options).isSuccess()) {
				instrumentation.addTransformer(classTransformer, true);
				if (options.isAttached()) {
					transformLoadedClasses(status);
				}
				if (status.isSuccess()) {
					controlFileService.start();
				}
			}
		} else {
			status.add(Status.createStatus(ControlFileMissing));
		}
		return status;
	}

	private Status loadControlFile(Status status, StartOptions options) {
		Agent model = loadModel(status, options.getControlFile());
		if (status.isSuccess()) {
			loadTargets(status, model);
			classPathService.updateClassPool(model.getClassPathDef());
		}
		return status;
	}

	public void transformLoadedClasses(Status status) {
		try {
			Map<String, Set<Class<?>>> map = getAllLoadedClasses();
			List<Class<?>> classesToTransform = new ArrayList<Class<?>>();
			for (String className : runtime.getTargetClasses()) {
				Set<Class<?>> classes = map.get(className);
				if (classes != null) {
					if (classes.size() == 1) {
						classesToTransform.add(classes.iterator().next());
					} else if (classes.size() == 0) {
						RuntimeClassNotFound.print(className);
					} else {
						RuntimeNonUniqueClass.print(className);
					}
				} else {
					RuntimeClassNotFound.print(className);
				}
			}
			if (!classesToTransform.isEmpty()) {
				instrumentation.retransformClasses(classesToTransform
						.toArray(new Class<?>[classesToTransform.size()]));
			}
		} catch (UnmodifiableClassException e) {
			ClassCanNotBeTransformed.print(e.getMessage());
		}
	}

	public void reloadConfiguration(Status status) {
		try {
			List<Class<?>> classDefinitions = new ArrayList<Class<?>>();
			for (String className : runtime.getTargetClasses()) {
				ClassData classData = runtime.getClassData(className);
				ClassLoader classLoader = classData.getClassLoader();
				Class<?> clazz = classLoader == null ? Class.forName(className)
						: classLoader.loadClass(className);
				classDefinitions.add(clazz);
			}
			classTransformer.setReset(true);
			if (!classDefinitions.isEmpty()) {
				instrumentation.retransformClasses(classDefinitions
						.toArray(new Class<?>[classDefinitions.size()]));
			}
			runtime.reset();
			classTransformer.setReset(false);
			loadControlFile(status, options);
			if (!classDefinitions.isEmpty()) {
				instrumentation.retransformClasses(classDefinitions
						.toArray(new Class<?>[classDefinitions.size()]));
			}
		} catch (ClassNotFoundException e) {
			RuntimeClassNotFound.print(e.getMessage());
		} catch (UnmodifiableClassException e) {
			ClassCanNotBeTransformed.print(e.getMessage());
		}
	}

	public Agent loadModel(Status status, File inputFile) {
		FileReader reader = null;
		try {
			reader = new FileReader(inputFile);
			AgentType agentType = modelSerializer.read(status, reader,
					AgentType.class);
			if (status.isSuccess()) {
				AbstractX2JModelFactory modelFactory = modelFactoryFactory
						.getFactory(agentType);
				return modelFactory.createModel(agentType, Agent.class);
			}
		} catch (FileNotFoundException e) {
			status.add(Status.createStatus(ControlFileNotFound));
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
		return null;
	}

	public void loadTargets(Status status, Agent model) {
		for (TargetDef targetDef : model.getTargets()) {
			for (MethodDef methodDef : targetDef.getMethods().values()) {
				loadMethod(methodDef);
			}
			for (ActionDef actionDef : targetDef.getActions()) {
				loadAction(actionDef);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadAction(ActionDef actionDef) {
		for (ScriptDef scriptDef : actionDef.getScriptDef()) {
			try {
				String className = createUniqueClassName();
				CtClass ctSuperClass;
				ctSuperClass = getCtClass(AbstractScript.class);
				CtClass ctClass = classPool.makeClass(className);
				ctClass.setSuperclass(ctSuperClass);
				CtClass ctReturnType = getCtClass(Object.class);
				CtMethod invokeMethod = CtNewMethod.make(ctReturnType,
						"invoke",
						new CtClass[] { getCtClass(CallState.class) },
						new CtClass[0], ajustSourceCode(scriptDef, scriptDef
								.getSourceCode()), ctClass);
				ctClass.addMethod(invokeMethod);
				Class<Script> clazz = ctClass.toClass();
				runtime.registerImplClass(className, clazz);
				scriptDef.setImplClassName(className);
			} catch (NotFoundException e) {
				ClassNotFound.print(e.getMessage());
			} catch (CannotCompileException e) {
				CompilationFailure.print(e.getMessage());
			}
		}
	}

	private String ajustSourceCode(ScriptDef scriptDef, String sourceCode) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(sourceCode);
		if (scriptDef.getAssignTo() == null) {
			builder.append("return null;");
		}
		builder.append("}");
		return builder.toString();
	}

	private CtClass getCtClass(Class<?> clazz) throws NotFoundException {
		return classPool.get(clazz.getName());
	}

	private void loadMethod(MethodDef methodDef) {
		methodParser.parseMethod(methodDef);
		runtime.registerMethod(methodDef);
	}

	public StartOptions getOptions() {
		return options;
	}

	private int classNameIndex = 0;

	synchronized private String createUniqueClassName() {
		return "generated.CipollinoClass" + ++classNameIndex;
	}

	private Map<String, Set<Class<?>>> getAllLoadedClasses() {
		Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
		Map<String, Set<Class<?>>> map = new HashMap<String, Set<Class<?>>>(
				20000);
		for (Class<?> clazz : loadedClasses) {
			Set<Class<?>> classes = map.get(clazz.getName());
			if (classes == null) {
				classes = new HashSet<Class<?>>();
				map.put(clazz.getName(), classes);
			}
			classes.add(clazz);
		}
		return map;
	}
}
