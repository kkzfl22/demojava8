package com.demo.effectivejava.seven.fortythird.good;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	 * 由于零长度的数组是不可以变的，而不可变的对象有可能被自由的共享
	 */
	private static final Cheesse[] EMPTY_CHEESE_ARRAY = new Cheesse[0];

	/**
	 * 这样会要求客户端中必须有额外的代码来处理null返回值
	 * 
	 * @return
	 */
	public Cheesse[] getCheeses() {
		return cheeseInStock.toArray(EMPTY_CHEESE_ARRAY);
	}

	public void add(Cheesse chee) {
		this.cheeseInStock.add(chee);
	}

	public List<Cheesse> getCheeseList() {
		if (cheeseInStock.isEmpty()) {
			return Collections.emptyList();
		} else {
			return new ArrayList<>(cheeseInStock);
		}
	}

	public static void main(String[] args) {
		TestCheesse tes = new TestCheesse();
		Cheesse ses = new Cheesse();
		ses.setNames("names");
		// tes.add(ses);
		// tes.add(ses);

		System.out.println(tes.getCheeses());

		System.out.println(tes.getCheeseList());
	}

}
