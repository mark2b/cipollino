package com.intellibird.cipollino.core.model;

import com.intellibird.cipollino.core.actions.Action;
import com.intellibird.cipollino.core.actions.ProfileAction;

public class ProfileActionDef extends ActionDef {

	@Override
	public Action createAction() {
		return new ProfileAction();
	}

}
