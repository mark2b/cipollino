package org.cipollino.core.services;

import static org.cipollino.core.error.ErrorCode.ClassCanNotBeTransformed;
import static org.cipollino.core.error.ErrorCode.ClassNotFound;
import static org.cipollino.core.error.ErrorCode.CompilationFailure;
import static org.cipollino.core.error.ErrorCode.ControlFileMissing;
import static org.cipollino.core.error.ErrorCode.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

import org.apache.commons.io.IOUtils;
import org.cipollino.core.error.ErrorException;
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

	public void start(Instrumentation instrumentation, StartOptions options) {
		this.options = options;
		this.instrumentation = instrumentation;
		if (options.getControlFile() != null) {
			loadControlFile();
			instrumentation.addTransformer(classTransformer, true);
			if (options.isAttached()) {
				transformLoadedClasses();
			}
			controlFileService.start();
		} else {
			throw new ErrorException(ControlFileMissing);
		}
	}

	/**
	 * Load the control file
	 * 
	 * @return list of affected classes
	 */
	private List<String> loadControlFile() {
		Agent model = loadModel(options.getControlFile());
		classPathService.updateClassPool(model.getClassPathDef());
		List<String> affectedClasses = loadTargets(model);
		return affectedClasses;
	}

	/**
	 * Load model from XML control file
	 * 
	 * @param inputFile
	 * @return model
	 */
	private Agent loadModel(File inputFile) {
		FileReader reader = null;
		try {
			reader = new FileReader(inputFile);
			AgentType agentType = modelSerializer.read(reader, AgentType.class);
			AbstractX2JModelFactory modelFactory = modelFactoryFactory
					.getFactory(agentType);
			return modelFactory.createModel(agentType, Agent.class);
		} catch (FileNotFoundException e) {
			throw new ErrorException(ControlFileNotFound, inputFile);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	private void transformLoadedClasses() {
		try {
			Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
			List<Class<?>> classesToTransform = new ArrayList<Class<?>>();
			for (Class<?> loadedClass : loadedClasses) {
				if (runtime.needTransformation(loadedClass.getName())) {
					classesToTransform.add(loadedClass);
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

	public void reloadConfiguration() {
		List<String> affectedClasses = loadControlFile();
		try {
			Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
			List<Class<?>> classesToTransform = new ArrayList<Class<?>>();
			for (Class<?> loadedClass : loadedClasses) {
				if (affectedClasses.contains(loadedClass.getName())) {
					classesToTransform.add(loadedClass);
				}
			}
			if (!classesToTransform.isEmpty()) {
				instrumentation.retransformClasses(classesToTransform
						.toArray(new Class<?>[classesToTransform.size()]));
			}
		} catch (UnmodifiableClassException e) {
			ClassCanNotBeTransformed.print(e.getMessage());
		}
		runtime.cleanDeletedItems();
	}

	/**
	 * Load targets.
	 * 
	 * @param model
	 * @return list of affected classes
	 */
	public List<String> loadTargets(Agent model) {
		List<String> affectedClasses = new ArrayList<String>();

		// Class name to methods list map for new methods.
		Map<String, List<MethodDef>> methodsMap = loadMethods(model
				.getTargets());

		// Mark unused transformed classes for delete
		Set<String> targetClasses = runtime.getTargetClasses();
		for (String targetClassName : targetClasses) {
			ClassData classData = runtime.getClassData(targetClassName);
			List<MethodDef> newMethods = methodsMap.get(targetClassName);
			List<MethodDef> oldMethods = classData.getMethods();
			// Mark old methods for delete
			for (MethodDef methodDef : oldMethods) {
				methodDef.setDeleted(true);
			}
			// Clean classData from old methods
			oldMethods.clear();
			if (methodsMap.containsKey(targetClassName)) {
				classData.setState(ClassState.TO_BE_RETRANSFORMED);
				classData.getMethods().addAll(newMethods);
				runtime.registerClass(targetClassName, classData);
			} else {
				classData.setState(ClassState.TO_BE_DELETED);
			}
			affectedClasses.add(targetClassName);
		}
		// Detect new classes
		for (Entry<String, List<MethodDef>> entry : methodsMap.entrySet()) {
			String className = entry.getKey();
			List<MethodDef> methods = entry.getValue();
			if (!targetClasses.contains(className)) {
				ClassData classData = new ClassData(className);
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

	/**
	 * Load methods
	 * 
	 * @param targets
	 * @return map class names to methods list.
	 */
	private Map<String, List<MethodDef>> loadMethods(List<TargetDef> targets) {
		Map<String, List<MethodDef>> map = new HashMap<String, List<MethodDef>>();
		for (TargetDef targetDef : targets) {
			for (MethodDef methodDef : targetDef.getMethods().values()) {
				if (loadMethod(methodDef)) {
					methodParser.parseMethod(methodDef);
					List<MethodDef> methods = map.get(methodDef.getClassName());
					if (methods == null) {
						methods = new ArrayList<MethodDef>(1);
						map.put(methodDef.getClassName(), methods);
					}
					methods.add(methodDef);
				}
			}
		}
		return map;
	}

	/**
	 * Load single method
	 * 
	 * @param methodDef
	 */
	private boolean loadMethod(MethodDef methodDef) {
		methodParser.parseMethod(methodDef);
		try {
			CtClass ctClass = classPool.get(methodDef.getClassName());
			CtMethod[] ctMethods = ctClass.getDeclaredMethods();
			CtMethod ctMethod = findCtMethod(methodDef, ctMethods);
			if (ctMethod == null) {
				ctMethods = ctClass.getMethods();
				ctMethod = findCtMethod(methodDef, ctMethods);
			}
			if (ctMethod != null) {
				methodDef.setClassName(ctMethod.getDeclaringClass().getName());
				methodDef.setSignature(ctMethod.getSignature());
				return true;
			}
			MethodNotFound.print(methodDef.getMethodName());
		} catch (NotFoundException e) {
			ClassNotFound.print(e.getMessage());
		}
		return false;
	}

	private CtMethod findCtMethod(MethodDef methodDef, CtMethod[] methods)
			throws NotFoundException {
		CtMethod method = null;
		for (CtMethod ctMethod : methods) {
			if (ctMethod.getName().equals(methodDef.getMethodName())) {
				if (methodDef.getArguments().size() == 0) {
					method = ctMethod;
					break;
				} else if (ctMethod.getParameterTypes().length == methodDef
						.getArguments().size()) {
					method = ctMethod;
					break;
				}
			}
		}
		return method;
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

	public StartOptions getOptions() {
		return options;
	}

	private int classNameIndex = 0;

	synchronized private String createUniqueClassName() {
		return "generated.CipollinoClass" + ++classNameIndex;
	}
}
