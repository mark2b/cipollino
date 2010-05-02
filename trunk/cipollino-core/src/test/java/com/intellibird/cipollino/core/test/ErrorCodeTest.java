package com.intellibird.cipollino.core.test;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.intellibird.cipollino.core.error.ErrorCode;

public class ErrorCodeTest {

	@Test
	public void getErrorCodeTest() {
		ErrorCode errorCode = ErrorCode.InternalError;
		assertEquals(errorCode.getCode(), "00001");
	}
}
