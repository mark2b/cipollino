package org.cipollino.logger.model;

import org.cipollino.core.actions.Action;
import org.cipollino.core.model.ActionDef;
import org.cipollino.logger.actions.LoggerAction;

public class LoggerActionDef extends ActionDef {

	private FormatDef beforeFormatDef;

	private FormatDef afterFormatDef;

	private FormatDef exceptionFormatDef;

	private StyleDef styleDef;

	@Override
	public Action createAction() {
		return new LoggerAction();
	}

	public FormatDef getBeforeFormatDef() {
		if (beforeFormatDef == null) {
			beforeFormatDef = createBeforeFormatDefault();
		}
		return beforeFormatDef;
	}

	public void setBeforeFormatDef(FormatDef beforeFormatDef) {
		this.beforeFormatDef = beforeFormatDef;
	}

	public FormatDef getAfterFormatDef() {
		if (afterFormatDef == null) {
			afterFormatDef = createAfterFormatDefault();
		}
		return afterFormatDef;
	}

	public void setAfterFormatDef(FormatDef afterFormatDef) {
		this.afterFormatDef = afterFormatDef;
	}

	public FormatDef getExceptionFormatDef() {
		if (exceptionFormatDef == null) {
			exceptionFormatDef = createExceptionFormatDefault();
		}
		return exceptionFormatDef;
	}

	public void setExceptionFormatDef(FormatDef formatDef) {
		this.exceptionFormatDef = afterFormatDef;
	}

	private FormatDef createBeforeFormatDefault() {
		FormatDef formatDef = new FormatDef();
		formatDef.setFormat("${method} < ${parameters}");
		return formatDef;
	}

	private FormatDef createAfterFormatDefault() {
		FormatDef formatDef = new FormatDef();
		formatDef.setFormat("${method} > ${return}");
		return formatDef;
	}

	private FormatDef createExceptionFormatDefault() {
		FormatDef formatDef = new FormatDef();
		formatDef.setFormat("${method} > throws ${exception}");
		return formatDef;
	}

	public StyleDef getStyleDef() {
		if (styleDef == null) {
			styleDef = new StyleDef();
		}
		return styleDef;
	}

	public void setStyleDef(StyleDef styleDef) {
		this.styleDef = styleDef;
	}
}
