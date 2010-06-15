package org.cipollino.core.test;

import static org.testng.Assert.assertEquals;

import org.cipollino.core.error.ErrorCode;
import org.testng.annotations.Test;

public class ErrorCodeTest {

	@Test
	public void getErrorCodeTest() {
		ErrorCode errorCode = ErrorCode.InternalError;
		assertEquals(errorCode.getCode(), "00001");
	}

	@Test
	public void formatMessageTest() {
		ErrorCode errorCode = ErrorCode.InternalError;
		assertEquals(errorCode.format("Error"), "(00001) Internal Error. Error.");
		assertEquals(ErrorCode.ControlFileNotFound.format("/file.xml"),
				"(00102) The Control file [/file.xml] not found.");
	}
}
