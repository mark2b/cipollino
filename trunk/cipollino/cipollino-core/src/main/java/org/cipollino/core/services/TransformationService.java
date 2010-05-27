package org.cipollino.core.services;

import static org.cipollino.core.error.ErrorCode.ClassCanNotBeTransformed;
import static org.cipollino.core.error.ErrorCode.ClassNotFound;
import static org.cipollino.core.error.ErrorCode.CompilationFailure;
import static org.cipollino.core.error.ErrorCode.ControlFileMissing;
import static org.cipollino.core.error.ErrorCode.ControlFileNotFound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
import org.cipollino.core.runtime.ClassState;
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

	public Status start(final Instrumentation instrumentation, final StartOptions options) {
		this.options = options;
		final Status status = Status.createStatus();
		this.instrumentation = instrumentation;
		if (options.getControlFile() != null) {
			loadControlFile(status);
			if (status.isSuccess()) {
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

	private List<String> loadControlFile(Status status) {
		List<String> affectedClasses = new ArrayList<String>();
		Agent model = loadModel(status, options.getControlFile());
		if (status.isSuccess()) {
			affectedClasses = loadTargets(status, model);
			classPathService.updateClassPool(model.getClassPathDef());
		}
		return affectedClasses;
	}

	public void transformLoadedClasses(Status status) {
		try {
			Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
			List<Class<?>> classesToTransform = new ArrayList<Class<?>>();
			for (Class<?> loadedClass : loadedClasses) {
				if (runtime.needTransformation(loadedClass.getName())) {
					System.out.println("TransformationService.transformLoadedClasses() " + loadedClass.getName());
					classesToTransform.add(loadedClass);
				}
			}
			if (!classesToTransform.isEmpty()) {
				instrumentation.retransformClasses(classesToTransform.toArray(new Class<?>[classesToTransform.size()]));
			}
		} catch (UnmodifiableClassException e) {
			ClassCanNotBeTransformed.print(e.getMessage());
		}
	}

	public void reloadConfiguration(Status status) {
		List<String> affectedClasses = loadControlFile(status);
		try {
			Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
			List<Class<?>> classesToTransform = new ArrayList<Class<?>>();
			for (Class<?> loadedClass : loadedClasses) {
				if (affectedClasses.contains(loadedClass.getName())) {
					classesToTransform.add(loadedClass);
				}
			}
			if (!classesToTransform.isEmpty()) {
				instrumentation.retransformClasses(classesToTransform.toArray(new Class<?>[classesToTransform.size()]));
			}
		} catch (UnmodifiableClassException e) {
			ClassCanNotBeTransformed.print(e.getMessage());
		}
		runtime.cleanDeletedItems();
	}

	public Agent loadModel(Status status, File inputFile) {
		FileReader reader = null;
		try {
			reader = new FileReader(inputFile);
			AgentType agentType = modelSerializer.read(status, reader, AgentType.class);
			if (status.isSuccess()) {
				AbstractX2JModelFactory modelFactory = modelFactoryFactory.getFactory(agentType);
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

	public List<String> loadTargets(Status status, Agent model) {
		List<String> affectedClasses = new ArrayList<String>();

		Map<String, List<MethodDef>> methodsMap = loadMethods(model.getTargets());

		// Mark unused transformed classes for delete
		Set<String> targetClasses = runtime.getTargetClasses();
		for (String targetClassName : targetClasses) {
			ClassData classData = runtime.getClassData(targetClassName);
			List<MethodDef> newMethods = methodsMap.get(targetClassName);
			List<MethodDef> oldMethods = classData.getMethods();
			// Prepare old methods for delete
			for (MethodDef methodDef : oldMethods) {
				methodDef.setDeleted(true);
			}
			oldMethods.clear();
			if (methodsMap.containsKey(targetClassName)) {
				classData.setState(ClassState.TO_BE_RETRANSFORMED);
				classData.getMethods().addAll(newMethods);

			} else {
				classData.setState(ClassState.TO_BE_DELETED);
			}
			affectedClasses.add(targetClassName);
		}
		// Detect new classes
		for (Entry<String, List<MethodDef>> entry : methodsMap.entrySet()) {
			String className = entry.getKey();
			List<MethodDef> methods = entry.getValue();
			if (!targetClasses.contains(entry.getKey())) {
				ClassData classData = new ClassData();
				classData.setState(ClassState.TO_BE_TRANSFORMED);
				classData.getMethods().addAll(methods);
				runtime.registerClass(className, classData);
			}
			affectedClasses.add(className);
		}

		for (TargetDef targetDef : model.getTargets()) {
			for (ActionDef actionDef : targetDef.getActions()) {
				loadAction(actionDef);
			}
		}
		return affectedClasses;
	}

	private Map<String, List<MethodDef>> loadMethods(List<TargetDef> targets) {
		Map<String, List<MethodDef>> map = new HashMap<String, List<MethodDef>>();
		for (TargetDef targetDef : targets) {
			for (MethodDef methodDef : targetDef.getMethods().values()) {
				methodParser.parseMethod(methodDef);
				List<MethodDef> methods = map.get(methodDef.getClassName());
				if (methods == null) {
					methods = new ArrayList<MethodDef>(1);
					map.put(methodDef.getClassName(), methods);
				}
				methods.add(methodDef);
			}
		}
		return map;
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
				CtMethod invokeMethod = CtNewMethod.make(ctReturnType, "invoke", new CtClass[] { getCtClass(CallState.class) }, new CtClass[0],
						ajustSourceCode(scriptDef, scriptDef.getSourceCode()), ctClass);
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

	public StartOptions getOptions() {
		return options;
	}

	private int classNameIndex = 0;

	synchronized private String createUniqueClassName() {
		return "generated.CipollinoClass" + ++classNameIndex;
	}
}
