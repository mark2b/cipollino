package org.cipollino.itests.app2;

public class Main {

	public static void main(String[] args) {
		A1 a1 = new A1();
		while (true) {
			a1.run1();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	static class A1 {
		public void run1() {
			System.out.println("Main.A1.run1()");
		}
	}
}
