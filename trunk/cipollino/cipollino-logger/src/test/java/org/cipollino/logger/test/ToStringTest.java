package org.cipollino.logger.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.swing.JFrame;

import org.cipollino.logger.format.ToString;
import org.testng.annotations.Test;


public class ToStringTest {

	@Test
	public void toStringTest() {
//		System.out.println("----------------------------");
//		System.out.println(toString.toString(new Data1()));
//		System.out.println("----------------------------");
		System.out.println(ToString.create().toString(new JFrame()));
//		System.out.println("----------------------------");
//		System.out.println(toString.toString(new Data1()));
//		System.out.println("----------------------------");
//		System.out.println(toString.toString(new Object[] { new Data1(),
//				new Data1() }));
		System.out.println(ToString.create().toString(System.getenv()));
//		System.out.println("----------------------------");
	}

	private static class Data1 {

		private int a = 1;

		private float b = 2f;

		private Integer c = 3;

		private String d = "123";

		private Date e = new Date();

		private String[] f = new String[] { "111", "222" };

		private int[] g = new int[] { 111, 222, 333 };

		private Collection<String> h = Arrays.asList(f);

		private Data1 i = this;

		private Data1 j = null;

		@Override
		public String toString() {
			return "Data1 [a=" + a + "]";
		}
	}
}
