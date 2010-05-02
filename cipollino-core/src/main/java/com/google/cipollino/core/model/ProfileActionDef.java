package com.google.cipollino.core.model;

import com.google.cipollino.core.actions.Action;
import com.google.cipollino.core.actions.ProfileAction;

public class ProfileActionDef extends ActionDef {

	@Override
	public Action createAction() {
		return new ProfileAction();
	}

}
