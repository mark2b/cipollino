package org.cipollino.core.test;

import org.cipollino.core.codegen.AfterMethodGenerator;
import org.cipollino.core.codegen.BeforeMethodGenerator;
import org.cipollino.core.codegen.OnExceptionGenerator;
import org.cipollino.core.codegen.OnFinallyGenerator;
import org.cipollino.core.model.MethodDef;
import org.testng.annotations.Test;

public class CodegenTest {

	@Test
	public void beforeMethodGeneratorTest() {
		BeforeMethodGenerator beforeMethodGenerator = new BeforeMethodGenerator();

		MethodDef methodDef = new MethodDef();
		beforeMethodGenerator.setMethodDef(methodDef);
		beforeMethodGenerator.generate();
	}

	@Test
	public void afterMethodGeneratorTest() {
		AfterMethodGenerator afterMethodGenerator = new AfterMethodGenerator();

		MethodDef methodDef = new MethodDef();
		afterMethodGenerator.setMethodDef(methodDef);
		afterMethodGenerator.generate();
	}

	@Test
	public void onExceptionGeneratorTest() {
		OnExceptionGenerator onExceptionGenerator = new OnExceptionGenerator();

		MethodDef methodDef = new MethodDef();
		onExceptionGenerator.setMethodDef(methodDef);
		onExceptionGenerator.generate();
	}

	@Test
	public void onFinallyGeneratorTest() {
		OnFinallyGenerator onFinallyGenerator = new OnFinallyGenerator();

		MethodDef methodDef = new MethodDef();
		onFinallyGenerator.setMethodDef(methodDef);
		onFinallyGenerator.generate();
	}
}
