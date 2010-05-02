package com.google.cipollino.core.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cipollino.core.error.ErrorCode;
import com.google.cipollino.core.error.Status;
import com.google.cipollino.core.error.Status.Severity;
import com.google.cipollino.core.inst.ClassTransformer;
import com.google.cipollino.core.model.ActionDef;
import com.google.cipollino.core.model.DirectiveDef;
import com.google.cipollino.core.model.Directives;
import com.google.cipollino.core.model.MethodDef;
import com.google.cipollino.core.model.ScriptDef;
import com.google.cipollino.core.parsers.MethodParser;
import com.google.cipollino.core.runtime.AbstractScript;
import com.google.cipollino.core.runtime.CallState;
import com.google.cipollino.core.runtime.ClassData;
import com.google.cipollino.core.runtime.Runtime;
import com.google.cipollino.core.runtime.Script;
import com.google.cipollino.core.runtime.StartOptions;
import com.google.cipollino.core.xml.AbstractX2JModelFactory;
import com.google.cipollino.core.xml.ModelSerializer;
import com.google.cipollino.core.xml.X2JModelFactoryFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.cipollino.core.schema.DirectivesType;

@Singleton
public class TransformationService {

	private static Logger logger = LoggerFactory.getLogger(TransformationService.class);

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
	private ClassPool classPool;

	public Status start(Instrumentation instrumentation, StartOptions options) {
		this.options = options;
		Status status = Status.createStatus(Severity.SUCCESS);
		this.instrumentation = instrumentation;
		if (options.getControlFile() != null) {
			loadDirectives(status, options.getControlFile());
		}
		if (status.isSuccess()) {
			instrumentation.addTransformer(classTransformer, true);
		}
		controlFileService.start();
		return status;
	}

	public void reloadDirectives(Status status, File inputFile) {
		try {
			List<Class<?>> classDefinitions = new ArrayList<Class<?>>();
			for (String className : runtime.getTransformedClasses()) {
				classDefinitions.add(Class.forName(className));
			}
			classTransformer.setReset(true);
			instrumentation.retransformClasses(classDefinitions.toArray(new Class<?>[0]));
			runtime.reset();
			classTransformer.setReset(false);
			loadDirectives(status, inputFile);
			instrumentation.retransformClasses(classDefinitions.toArray(new Class<?>[0]));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnmodifiableClassException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadDirectives(Status status, File inputFile) {
		FileReader reader = null;
		try {
			reader = new FileReader(inputFile);
			DirectivesType directivesType = modelSerializer.read(status, reader, DirectivesType.class);
			if (status.isSuccess()) {
				AbstractX2JModelFactory modelFactory = modelFactoryFactory.getFactory(directivesType);
				Directives directives = modelFactory.createModel(directivesType, Directives.class);
				if (directives != null) {
					for (DirectiveDef directiveDef : directives.getDirectives()) {
						for (MethodDef methodDef : directiveDef.getMethods().values()) {
							loadMethod(methodDef);
						}
						for (ActionDef actionDef : directiveDef.getActions()) {
							loadAction(actionDef);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			ErrorCode errorCode = ErrorCode.ControlFileNotFound;
			status.add(Status.createStatus(Severity.ERROR, errorCode.getCode(), errorCode.formatMessage(inputFile
					.getAbsoluteFile())));
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
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
				CtMethod invokeMethod = CtNewMethod.make(ctReturnType, "invoke",
						new CtClass[] { getCtClass(CallState.class) }, new CtClass[0], ajustSourceCode(scriptDef,
								scriptDef.getSourceCode()), ctClass);
				ctClass.addMethod(invokeMethod);
				Class<Script> clazz = ctClass.toClass();
				runtime.registerImplClass(className, clazz);
				scriptDef.setImplClassName(className);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
}
