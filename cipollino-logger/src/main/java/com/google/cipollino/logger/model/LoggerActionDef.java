package com.google.cipollino.logger.model;

import com.google.cipollino.core.actions.Action;
import com.google.cipollino.core.model.ActionDef;
import com.google.cipollino.logger.actions.LoggerAction;

public class LoggerActionDef extends ActionDef {

	private FormatDef beforeFormatDef;

	private FormatDef afterFormatDef;

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

	private FormatDef createBeforeFormatDefault() {
		FormatDef formatDef = new FormatDef();
		formatDef.setFormat("${parameters}");
		formatDef.setStyle("text");
		return formatDef;
	}

	private FormatDef createAfterFormatDefault() {
		FormatDef formatDef = new FormatDef();
		formatDef.setFormat("${return}");
		formatDef.setStyle("text");
		return formatDef;
	}
}
