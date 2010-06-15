package org.cipollino.core.services;

import static org.cipollino.core.error.ErrorCode.ClassNotFound;
import static org.cipollino.core.error.ErrorCode.CompilationFailure;
import static org.cipollino.core.error.ErrorCode.Trace;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import org.cipollino.core.codegen.AfterMethodGenerator;
import org.cipollino.core.codegen.BeforeMethodGenerator;
import org.cipollino.core.codegen.OnExceptionGenerator;
import org.cipollino.core.codegen.OnFinallyGenerator;
import org.cipollino.core.model.MethodDef;
import org.cipollino.core.runtime.ClassData;
import org.cipollino.core.runtime.ClassState;
import org.cipollino.core.runtime.Runtime;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ClassTransformerService implements ClassFileTransformer {

	@Inject
	private Runtime runtime;

	@Inject
	private ClassPathService classPathService;

	@Override
	public byte[] transform(ClassLoader classLoader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classFQN = className.replace('/', '.');
		if (runtime.needTransformation(classFQN)) {
			ClassData classData = runtime.getClassData(classFQN);
			// Save origin byte code for first time loaded class
			if (!classData.isLoaded()) {
				classData.setOriginByteCode(classfileBuffer);
				classData.setClassLoader(classLoader);
			}

			switch (classData.getState()) {
			case TO_BE_TRANSFORMED:
				classfileBuffer = transformBytecode(classfileBuffer, classData);
				classData.setState(ClassState.TRANSFORMED);
				break;
			case TO_BE_RETRANSFORMED:
				classfileBuffer = transformBytecode(classData
						.getOriginBytecode(), classData);
				classData.setState(ClassState.TRANSFORMED);
				break;
			case TO_BE_DELETED:
				classfileBuffer = classData.getOriginBytecode();
				classData.setState(ClassState.DELETED);
				break;
			}
		}
		return classfileBuffer;
	}

	private byte[] transformBytecode(byte[] classfileBuffer, ClassData classData) {
		try {
			CtClass cl = classPathService.getClassPool().makeClass(
					new java.io.ByteArrayInputStream(classfileBuffer), false);
			List<MethodDef> methods = classData.getMethods();
			for (MethodDef methodDef : methods) {
				transformMethod(methodDef, cl);
			}
			classfileBuffer = cl.toBytecode();
			Trace.print("Transformed " + classData.getClassName() + " class.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classfileBuffer;
	}

	private void transformMethod(MethodDef methodDef, CtClass ctClass) {
		try {
			CtMethod srcCtMethod = ctClass.getMethod(methodDef.getMethodName(),
					methodDef.getSignature());
			boolean staticMethod = (srcCtMethod.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
			methodDef.setStaticMethod(staticMethod);
			srcCtMethod.insertBefore(buildBeforeMethodCode(methodDef));
			srcCtMethod.insertAfter(buildAfterMethodCode());
			srcCtMethod.addCatch(buildOnExceptionCode(), classPathService
					.getClassPool().get(Throwable.class.getName()));
			srcCtMethod.insertAfter(buildOnFinally(), true);
			Trace.print("Transformed " + methodDef.getClassName() + "."
					+ methodDef.getMethodName() + " method.");
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
}
