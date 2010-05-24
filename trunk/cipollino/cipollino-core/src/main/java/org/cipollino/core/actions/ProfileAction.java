package org.cipollino.core.actions;
//package org.cipollino.core.actions;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.cipollino.core.runtime.CallState;
//
//public class ProfileAction extends AbstractAction {
//
//	Logger logger = LoggerFactory.getLogger(ProfileAction.class);
//
//	@Override
//	public void executeBefore(CallState callState) {
//		Long startTime = System.currentTimeMillis();
//		callState.getStateMap().put("startTime", startTime);
//	}
//
//	@Override
//	public void executeAfter(CallState callState) {
//		if (callState.isSuccess()) {
//			Long tookTime = System.currentTimeMillis() - (Long) callState.getStateMap().get("startTime");
//			logger.info("Method - " + callState.getMethodDef().getMethodName() + ":" + tookTime + " nsec");
//		}
//	}
//}
