//package com.intellibird.cipollino.core.xml;
//
//import java.util.List;
//
//import com.google.inject.Singleton;
//import com.intellibird.cipollino.core.model.ActionDef;
//import com.intellibird.cipollino.core.model.DirectiveDef;
//import com.intellibird.cipollino.core.model.Directives;
//import com.intellibird.cipollino.core.model.FormatDef;
//import com.intellibird.cipollino.core.model.MethodDef;
//import com.intellibird.cipollino.core.model.ProfileActionDef;
//import com.intellibird.cipollino.core.model.ScriptDef;
//import com.intellibird.cipollino.core.model.TraceActionDef;
//import com.intellibird.cipollino.core.schema.ActionType;
//import com.intellibird.cipollino.core.schema.DirectiveType;
//import com.intellibird.cipollino.core.schema.DirectivesType;
//import com.intellibird.cipollino.core.schema.FormatType;
//import com.intellibird.cipollino.core.schema.MethodType;
//import com.intellibird.cipollino.core.schema.PhaseType;
//import com.intellibird.cipollino.core.schema.ProfileActionType;
//import com.intellibird.cipollino.core.schema.ScriptType;
//import com.intellibird.cipollino.core.schema.TraceActionType;
//
//@Singleton
//public class X2JModelFactory extends AbstractX2JModelFactory {
//
//	public Directives create(DirectivesType source) {
//		Directives target = new Directives();
//
//		for (DirectiveType targetType : source.getDirective()) {
//			DirectiveDef directiveDef = createModel(targetType, DirectiveDef.class);
//			target.getDirectives().add(directiveDef);
//		}
//
//		return target;
//	}
//
//	public DirectiveDef create(DirectiveType source) {
//		DirectiveDef target = new DirectiveDef();
//		for (MethodType methodType : source.getMethod()) {
//			MethodDef method = createModel(methodType, MethodDef.class);
//			method.setDirectiveDef(target);
//			target.getMethods().put(methodType.getName(), method);
//		}
//		for (ActionType actionType : source.getAction()) {
//			ActionDef actionDef = createModel(actionType, ActionDef.class);
//			target.getActions().add(actionDef);
//		}
//		return target;
//	}
//
//	// public ActionDef create(ActionType source) {
//	// ActionDef target = null;
//	// if (source instanceof TraceActionType) {
//	// target = this.create(source, TraceActionDef.class);
//	// } else if (source instanceof ProfileActionType) {
//	// target = this.create(source, ProfileActionDef.class);
//	// }
//	// if (target == null) {
//	// throw new ErrorException(ErrorCode.XmlParsingError);
//	// }
//	// // target.setScript(source.getScript());
//	// return target;
//	// }
//
//	public TraceActionDef create(TraceActionType source) {
//		TraceActionDef target = new TraceActionDef();
//		List<ScriptType> scriptTypes = source.getScript();
//		if (scriptTypes != null) {
//			for (ScriptType scriptType : scriptTypes) {
//				target.getScriptDef().add(createModel(scriptType, ScriptDef.class));
//			}
//		}
//		List<FormatType> formatTypes = source.getFormat();
//		if (source.getFormat() != null) {
//			for (FormatType formatType : formatTypes) {
//				if (formatType.getPhase().equals(PhaseType.BEFORE)) {
//					target.setBeforeFormatDef(createModel(formatType, FormatDef.class));
//				} else if (formatType.getPhase().equals(PhaseType.AFTER)) {
//					target.setAfterFormatDef(createModel(formatType, FormatDef.class));
//				}
//			}
//		}
//		return target;
//	}
//
//	public ProfileActionDef create(ProfileActionType source) {
//		ProfileActionDef target = new ProfileActionDef();
//		List<ScriptType> scriptsType = source.getScript();
//		if (scriptsType != null) {
//			for (ScriptType scriptType : scriptsType) {
//				target.getScriptDef().add(createModel(scriptType, ScriptDef.class));
//			}
//		}
//		return target;
//	}
//
//	public MethodDef create(MethodType source) {
//		MethodDef target = new MethodDef();
//		target.setName(source.getName());
//		return target;
//	}
//
//	public ScriptDef create(ScriptType source) {
//		ScriptDef target = new ScriptDef();
//		target.setSourceCode(source.getValue());
//		target.setAssignTo(source.getAssignTo());
//		return target;
//	}
//
//	public FormatDef create(FormatType source) {
//		FormatDef target = new FormatDef();
//		target.setFormat(source.getValue());
//		target.setStyle(source.getStyle());
//		return target;
//	}
//}
