package com.intellibird.cipollino.core.schema;

import com.google.inject.Singleton;
import com.intellibird.cipollino.core.annotations.ModelFactory;
import com.intellibird.cipollino.core.model.ActionDef;
import com.intellibird.cipollino.core.model.DirectiveDef;
import com.intellibird.cipollino.core.model.Directives;
import com.intellibird.cipollino.core.model.MethodDef;
import com.intellibird.cipollino.core.model.ScriptDef;
import com.intellibird.cipollino.core.xml.AbstractX2JModelFactory;

@ModelFactory("com.intellibird.cipollino.core.schema.ModelFactory")
@Singleton
public class X2JModelFactory extends AbstractX2JModelFactory {

	public Directives create(DirectivesType source) {
		Directives target = new Directives();

		for (DirectiveType targetType : source.getDirective()) {
			DirectiveDef directiveDef = createModel(targetType, DirectiveDef.class);
			target.getDirectives().add(directiveDef);
		}

		return target;
	}

	public DirectiveDef create(DirectiveType source) {
		DirectiveDef target = new DirectiveDef();
		for (MethodType methodType : source.getMethod()) {
			MethodDef method = createModel(methodType, MethodDef.class);
			method.setDirectiveDef(target);
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
		return target;
	}

	public ScriptDef create(ScriptType source) {
		ScriptDef target = new ScriptDef();
		target.setSourceCode(source.getValue());
		target.setAssignTo(source.getAssignTo());
		return target;
	}

}
