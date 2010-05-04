package com.google.cipollino.logger.schema;

import java.util.List;

import com.google.cipollino.core.annotations.ModelFactory;
import com.google.cipollino.core.model.MethodDef;
import com.google.cipollino.core.model.ProfileActionDef;
import com.google.cipollino.core.model.ScriptDef;
import com.google.cipollino.core.schema.MethodType;
import com.google.cipollino.core.schema.PhaseType;
import com.google.cipollino.core.schema.ProfileActionType;
import com.google.cipollino.core.schema.ScriptType;
import com.google.cipollino.core.xml.AbstractX2JModelFactory;
import com.google.cipollino.logger.model.FormatDef;
import com.google.cipollino.logger.model.LoggerActionDef;
import com.google.inject.Singleton;

@ModelFactory("com.google.cipollino.logger.schema.ModelFactory")
@Singleton
public class X2JModelFactory extends AbstractX2JModelFactory {

	public LoggerActionDef create(LoggerActionType source) {
		LoggerActionDef target = new LoggerActionDef();
		List<ScriptType> scriptTypes = source.getScript();
		if (scriptTypes != null) {
			for (ScriptType scriptType : scriptTypes) {
				target.getScriptDef().add(createModel(scriptType, ScriptDef.class));
			}
		}
		List<FormatType> formatTypes = source.getFormat();
		if (source.getFormat() != null) {
			for (FormatType formatType : formatTypes) {
				if (formatType.getPhase().equals(PhaseType.BEFORE)) {
					target.setBeforeFormatDef(createModel(formatType, FormatDef.class));
				} else if (formatType.getPhase().equals(PhaseType.AFTER)) {
					target.setAfterFormatDef(createModel(formatType, FormatDef.class));
				}
			}
		}
		return target;
	}

	public ProfileActionDef create(ProfileActionType source) {
		ProfileActionDef target = new ProfileActionDef();
		List<ScriptType> scriptsType = source.getScript();
		if (scriptsType != null) {
			for (ScriptType scriptType : scriptsType) {
				target.getScriptDef().add(createModel(scriptType, ScriptDef.class));
			}
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

	public FormatDef create(FormatType source) {
		FormatDef target = new FormatDef();
		target.setFormat(source.getValue());
		target.setStyle(source.getStyle());
		return target;
	}
}
