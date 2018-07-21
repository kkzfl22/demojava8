package com.demo.effectivejava.seven.fortyeight.bad;

public class BadCode {

	public static void main(String[] args) {
		System.out.println(1.03 - .42);
		System.out.println(1.00 - 9 * .10);

		// 计算一个题
		// 假如口袋里有￥1，你看到货架 上有一批好吃的糖果，标价分为0.1￥，0，2￥，0.3￥，等等，
		// 一直到￥1，你计算从标签 为0.1人糖果开始,每种买一颗糖，一直到不能支付货架上下一种糖果肉的价格为目，计算可以买多少颗糖
		count();
	}

	public static void count() {
		double funds = 1.00;
		int itemsBounght = 0;

		for (double price = 0.1; funds >= price; price += .10) {
			funds -= price;
			itemsBounght++;
		}
		System.out.println(itemsBounght + " items bounght.");
		System.out.println("Change : $" + funds);

	}

}
