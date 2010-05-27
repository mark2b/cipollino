package org.cipollino.itests.app1;

import java.util.Calendar;
import java.util.Date;

public class Main {

	public static void main(String[] args) {
		Main app = new Main();
		app.convert(new Date());
		app.convertCalendar(Calendar.getInstance());
		app.concat("Hello,", "World !!!");
	}

	public String convert(Date date) {
		return date.toString();
	}

	public Date convertCalendar(Calendar calendar) {
		return calendar.getTime();
	}

	public String concat(String s1, String s2) {
		return s1 + " " + s2;
	}

}
