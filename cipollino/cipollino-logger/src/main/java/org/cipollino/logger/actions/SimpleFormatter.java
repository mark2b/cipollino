package org.cipollino.logger.actions;

import org.cipollino.logger.format.ToString;
import org.cipollino.logger.model.StyleDef;

public class SimpleFormatter extends AbstractFormatter {

	final private StyleDef styleDef;

	public SimpleFormatter(StyleDef styleDef) {
		this.styleDef = styleDef;
	}

	protected String formatParameter(Object object) {
		return ToString.create().formatted(styleDef.isFormatted())
				.printParentFields(styleDef.isPrintParentFields()).useToString(
						styleDef.isUseToString()).toString(object);
	}
}
