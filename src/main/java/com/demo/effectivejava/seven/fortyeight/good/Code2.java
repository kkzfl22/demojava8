package com.demo.effectivejava.seven.fortyeight.good;

public class Code2 {

	public static void main(String[] args) {
		int itemBought = 0;
		int funds = 100;
		for (int price = 10; funds >= price; price += 10) {
			itemBought++;
			funds -= price;
		}
		System.out.println(itemBought + " items bounght.");
		System.out.println("Memory left over:" + funds + " cents");
	}

}
