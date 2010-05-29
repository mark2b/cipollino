package org.cipollino.itests.app2;

public class Main {

	public static void main(String[] args) {
		Main a1 = new Main();
		for (int i = 0; i < 10; i++) {
			a1.run1();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
		System.exit(0);
	}

	public void run1() {
		System.out.println("Main.A1.run1()");
	}
}
