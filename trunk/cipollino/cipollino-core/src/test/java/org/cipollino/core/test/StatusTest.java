package org.cipollino.core.test;

import static org.testng.Assert.assertTrue;

import org.cipollino.core.error.Status;
import org.testng.annotations.Test;


public class StatusTest {

	@Test
	public void statusTest() {
		Status status = Status.createStatus();
		assertTrue(status.isSuccess());
	}
}
