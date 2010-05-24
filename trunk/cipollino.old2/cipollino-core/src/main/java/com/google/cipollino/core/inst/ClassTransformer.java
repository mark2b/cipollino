package com.google.cipollino.core.inst;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import com.google.cipollino.core.model.MethodDef;
import com.google.cipollino.core.runtime.ClassData;
import com.google.cipollino.core.runtime.Runtime;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.cipollino.core.codegen.AfterMethodGenerator;
import com.google.cipollino.core.codegen.BeforeMethodGenerator;
import com.google.cipollino.core.codegen.OnExceptionGenerator;
import com.google.cipollino.core.codegen.OnFinallyGenerator;

@Singleton
public class ClassTransformer implements ClassFileTransformer {

	@Inject
	private Runtime runtime;

	@Inject
	private ClassPool classPool;

	private boolean reset = false;

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		String classFQN = className.replace('/', '.');
		if (runtime.needTransformation(classFQN)) {
			ClassData classData = runtime.getClassData(classFQN);
			// Save origin byte code for first time loaded class
			if (classData != null && !classData.isLoaded()) {
				classData.setOriginByteCode(classfileBuffer);
			}
			if (reset) {
				classfileBuffer = classData.getOriginByteCode();
			} else {
				try {
					CtClass cl = classPool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer), false);
					List<MethodDef> methods = runtime.getMethods(classFQN);
					for (MethodDef methodDef : methods) {
						transformMethod(methodDef, cl);
					}
					classfileBuffer = cl.toBytecode();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return classfileBuffer;
	}

	private void transformMethod(MethodDef methodDef, CtClass ctClass) throws CannotCompileException, NotFoundException {
		CtMethod srcCtMethod = getCtMethodByMethod(ctClass, methodDef);
		if (srcCtMethod != null) {
			srcCtMethod.insertBefore(buildBeforeMethodCode(methodDef));
			srcCtMethod.insertAfter(buildAfterMethodCode());
			srcCtMethod.addCatch(buildOnExceptionCode(), classPool.get(Throwable.class.getName()));
			srcCtMethod.insertAfter(buildOnFinally(), true);
		}
	}

	private String buildBeforeMethodCode(MethodDef methodDef) {
		BeforeMethodGenerator codegen = new BeforeMethodGenerator();
		codegen.setMethodDef(methodDef);
		return codegen.generate();
	}

	private String buildAfterMethodCode() {
		AfterMethodGenerator codegen = new AfterMethodGenerator();
		return codegen.generate();
	}

	private String buildOnExceptionCode() {
		OnExceptionGenerator codegen = new OnExceptionGenerator();
		return codegen.generate();
	}

	private String buildOnFinally() {
		OnFinallyGenerator codegen = new OnFinallyGenerator();
		return codegen.generate();
	}

	private CtMethod getCtMethodByMethod(CtClass ctClass, MethodDef method) throws NotFoundException {
		List<CtClass> argumentClasses = new ArrayList<CtClass>();
		for (Entry<String, String> entry : method.getArguments().entrySet()) {
			argumentClasses.add(classPool.get(entry.getValue()));
		}
		CtMethod matchedMethod = ctClass.getDeclaredMethod(method.getMethodName(), argumentClasses
				.toArray(new CtClass[0]));
		return matchedMethod;
	}

	public boolean isReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}
}
