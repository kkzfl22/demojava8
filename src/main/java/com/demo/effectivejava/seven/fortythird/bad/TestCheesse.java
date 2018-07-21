package com.demo.effectivejava.seven.fortythird.bad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Cheesse {

	public static final Cheesse STLITON = new Cheesse();

	private String names;

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}
}

public class TestCheesse {

	private final List<Cheesse> cheeseInStock = new ArrayList<>();

	/**
	 * 这样会要求客户端中必须有额外的代码来处理null返回值
	 * 
	 * @return
	 */
	public Cheesse[] getCheeses() {
		if (cheeseInStock.size() == 0) {
			return null;
		}

		return cheeseInStock.toArray(new Cheesse[0]);
	}

	public void runGet() {
		TestCheesse test = new TestCheesse();
		Cheesse[] cheese = test.getCheeses();
		if (cheese != null && Arrays.asList(cheese).contains(Cheesse.STLITON)) {
			System.out.println("Jolly good ,just the thing");
		}
	}

	public void runGet2() {
		TestCheesse test = new TestCheesse();
		if (Arrays.asList(test.getCheeses()).contains(Cheesse.STLITON)) {
			System.out.println("Jolly good ,just the thing");
		}
	}

	public static void main(String[] args) {

	}

}
