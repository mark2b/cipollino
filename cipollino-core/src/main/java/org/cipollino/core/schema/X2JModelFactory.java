package org.cipollino.core.schema;

import java.util.List;

import org.cipollino.core.annotations.ModelFactory;
import org.cipollino.core.model.ActionDef;
import org.cipollino.core.model.ClassPathDef;
import org.cipollino.core.model.MethodDef;
import org.cipollino.core.model.Model;
import org.cipollino.core.model.ParameterDef;
import org.cipollino.core.model.ScriptDef;
import org.cipollino.core.model.TargetDef;
import org.cipollino.core.xml.AbstractX2JModelFactory;

import com.google.inject.Singleton;

@ModelFactory("org.cipollino.core.schema.ModelFactory")
@Singleton
public class X2JModelFactory extends AbstractX2JModelFactory {

	public Model create(SystemType source) {
		Model target = new Model();

		CompilerType compilerType = source.getCompiler();
		if (compilerType != null) {
			target.setClassPathDef(createModel(compilerType.getClassPath(),
					ClassPathDef.class));
		}

		for (TargetType targetType : source.getTarget()) {
			TargetDef targetDef = createModel(targetType, TargetDef.class);
			target.getTargets().add(targetDef);
		}

		return target;
	}

	public ClassPathDef create(ClassPathType source) {
		ClassPathDef target = new ClassPathDef();

		for (String classes : source.getClasses()) {
			target.getClasses().add(classes);
		}
		for (String jar : source.getJar()) {
			target.getJar().add(jar);
		}
		for (String jar : source.getJar()) {
			target.getJar().add(jar);
		}
		for (String dir : source.getDir()) {
			target.getDir().add(dir);
		}
		for (String path : source.getPath()) {
			target.getPath().add(path);
		}
		return target;
	}

	public TargetDef create(TargetType source) {
		TargetDef target = new TargetDef();
		for (MethodType methodType : source.getMethod()) {
			MethodDef method = createModel(methodType, MethodDef.class);
			method.setTargetDef(target);
			target.getMethods().put(methodType.getName(), method);
		}
		for (ActionType actionType : source.getAction()) {
			ActionDef actionDef = createModel(actionType, ActionDef.class);
			target.getActions().add(actionDef);
		}
		return target;
	}

	public MethodDef create(MethodType source) {
		MethodDef target = new MethodDef();
		target.setName(source.getName());
		for (MethodParameterType parameterType : source.getParameter()) {
			ParameterDef parameterDef = createModel(parameterType,
					ParameterDef.class);
			target.getParameters().put(parameterDef.getIndex(), parameterDef);
		}
		return target;
	}

	public ParameterDef create(MethodParameterType source) {
		ParameterDef target = new ParameterDef();
		target.setIndex(source.getIndex());
		target.setName(source.getName());
		target.setUpdatable(source.isUpdatable());
		return target;
	}

	public ScriptDef create(ScriptType source) {
		ScriptDef target = new ScriptDef();
		target.setSourceCode(source.getValue());
		target.setAssignTo(source.getAssignTo());
		return target;
	}

	public ActionDef create(ActionType source) {
		ActionDef target = new ActionDef();
		apply(source, target);
		return target;
	}

	protected void apply(ActionType source, ActionDef target) {
		List<ScriptType> scriptTypes = source.getScript();
		if (scriptTypes != null) {
			for (ScriptType scriptType : scriptTypes) {
				target.getScriptDef().add(
						createModel(scriptType, ScriptDef.class));
			}
		}
	}
}