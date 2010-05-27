package org.cipollino.itests.app1;

public class Main {

	public static void main(String[] args) {
		Main app = new Main();
		while (true) {
			app.run1();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	public void run1() {
		System.err.println("Main.A1.run1()");
	}
}
