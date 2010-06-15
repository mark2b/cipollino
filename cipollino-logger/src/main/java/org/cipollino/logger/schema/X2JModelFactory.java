package org.cipollino.logger.schema;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cipollino.core.annotations.ModelFactory;
import org.cipollino.core.model.MethodDef;
import org.cipollino.core.schema.MethodType;
import org.cipollino.core.schema.PhaseType;
import org.cipollino.logger.model.FormatDef;
import org.cipollino.logger.model.LoggerActionDef;
import org.cipollino.logger.model.StyleDef;

import com.google.inject.Singleton;

@ModelFactory("org.cipollino.logger.schema.ModelFactory")
@Singleton
public class X2JModelFactory extends org.cipollino.core.schema.X2JModelFactory {

	public LoggerActionDef create(LoggerActionType source) {
		LoggerActionDef target = new LoggerActionDef();
		apply(source, target);

		List<FormatType> formatTypes = source.getFormat();
		if (source.getFormat() != null) {
			for (FormatType formatType : formatTypes) {
				if (formatType.getPhase().equals(PhaseType.BEFORE)) {
					target.setBeforeFormatDef(createModel(formatType, FormatDef.class));
				} else if (formatType.getPhase().equals(PhaseType.AFTER)) {
					target.setAfterFormatDef(createModel(formatType, FormatDef.class));
				} else if (formatType.getPhase().equals(PhaseType.EXCEPTION)) {
					target.setExceptionFormatDef(createModel(formatType, FormatDef.class));
				}
			}
		}
		if (source.getStyle() != null) {
			target.setStyleDef(createModel(source.getStyle(), StyleDef.class));
		}
		return target;
	}

	public MethodDef create(MethodType source) {
		MethodDef target = new MethodDef();
		target.setName(source.getName());
		return target;
	}

	public FormatDef create(FormatType source) {
		FormatDef target = new FormatDef();
		String value = StringUtils.defaultIfEmpty(source.getValue(), "").trim();
		target.setFormat(value);
		return target;
	}

	public StyleDef create(StyleType source) {
		StyleDef target = new StyleDef();
		target.setFormatted(source.isFormatted());
		target.setPrintParentFields((source.isPrintParentFields()));
		target.setUseToString(source.isUseToString());
		return target;
	}
}
