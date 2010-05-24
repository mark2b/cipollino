package org.cipollino.core.inst;

import static org.cipollino.core.error.ErrorCode.ClassNotFound;
import static org.cipollino.core.error.ErrorCode.CompilationFailure;
import static org.cipollino.core.error.ErrorCode.MethodNotFound;
import static org.cipollino.core.error.ErrorCode.Trace;

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

import org.cipollino.core.codegen.AfterMethodGenerator;
import org.cipollino.core.codegen.BeforeMethodGenerator;
import org.cipollino.core.codegen.OnExceptionGenerator;
import org.cipollino.core.codegen.OnFinallyGenerator;
import org.cipollino.core.model.MethodDef;
import org.cipollino.core.runtime.ClassData;
import org.cipollino.core.runtime.Runtime;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ClassTransformer implements ClassFileTransformer {

	@Inject
	private Runtime runtime;

	@Inject
	private ClassPool classPool;

	private boolean reset = false;

	@Override
	public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
			throws IllegalClassFormatException {
		String classFQN = className.replace('/', '.');
		if (runtime.needTransformation(classFQN)) {
			ClassData classData = runtime.getClassData(classFQN);
			// Save origin byte code for first time loaded class
			if (classData != null && !classData.isLoaded()) {
				classData.setOriginByteCode(classfileBuffer);
				classData.setClassLoader(classLoader);
				Trace.print("Transformed " + classFQN + " class.");
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

	private void transformMethod(MethodDef methodDef, CtClass ctClass) {
		try {
			CtMethod srcCtMethod = getCtMethodByMethod(ctClass, methodDef);
			if (srcCtMethod != null) {
				srcCtMethod.insertBefore(buildBeforeMethodCode(methodDef));
				srcCtMethod.insertAfter(buildAfterMethodCode());
				srcCtMethod.addCatch(buildOnExceptionCode(), classPool.get(Throwable.class.getName()));
				srcCtMethod.insertAfter(buildOnFinally(), true);
				Trace.print("Transformed " + methodDef.getClassName() + "." + methodDef.getMethodName() + " method.");
			}
		} catch (NotFoundException e) {
			ClassNotFound.print(e.getMessage());
		} catch (CannotCompileException e) {
			if (e.getCause() != null) {
				if (e.getCause() instanceof NotFoundException) {
					ClassNotFound.print(e.getCause().getMessage());
				} else {
					CompilationFailure.print(e.getMessage());
				}
			} else {
				CompilationFailure.print(e.getMessage());
			}
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

	private CtMethod getCtMethodByMethod(CtClass ctClass, MethodDef method) {
		try {
			List<CtClass> argumentClasses = new ArrayList<CtClass>();
			for (Entry<String, String> entry : method.getArguments().entrySet()) {
				argumentClasses.add(classPool.get(entry.getValue()));
			}
			CtMethod matchedMethod = ctClass.getDeclaredMethod(method.getMethodName());
			return matchedMethod;
		} catch (NotFoundException e) {
			MethodNotFound.print(method.getMethodName());
			return null;
		}
	}

	public boolean isReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}
}
