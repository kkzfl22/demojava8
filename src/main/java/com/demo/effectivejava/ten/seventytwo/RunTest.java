package com.demo.effectivejava.ten.seventytwo;

import java.util.concurrent.ThreadLocalRandom;

public class RunTest {

	public static void main(String[] args) {

		SlowCountDownLatch countDown = new SlowCountDownLatch(2);

		Runnable run1 = new Runnable() {
			public void run() {
				try {
					int radnInt = ThreadLocalRandom.current().nextInt(9000);
					System.out.println("rand int :" + radnInt);
					Thread.sleep(radnInt);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				countDown.countDown();

				System.out.println("over");

			}
		};

		new Thread(run1).start();
		new Thread(run1).start();

		countDown.await();

		System.out.println("main over");

	}

}
