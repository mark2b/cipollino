package org.cipollino.itests.app2;

public class Main {

	public static void main(String[] args) {
		Main a1 = new Main();
		while (true) {
			a1.run1();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	public void run1() {
		System.out.println("Main.A1.run1()");
	}
}
