package com.demo.effectivejava.genericity.pecs;

/**
 * 进行泛型 的测试
 * 
 * @since 2018年6月6日 下午3:33:27
 * @version 0.0.1
 * @author liujun
 */
public class GenericityTest {

	public static void main(String[] args) {
		Plate<? extends Fruit> p = new Plate<>(new Apple());

		// // 不能存入任何元素
		// p.set(new Fruit()); // Error
		// p.set(new Apple()); // Error
		// 读取出来的东西只能存放在Fruit或它的基类里。
		Fruit newFruit1 = p.get();
		System.out.println(newFruit1);
		Object newFruit2 = p.get();
		System.out.println(newFruit2);
		// Apple newFruit3 = p.get(); // Error

		Plate<? super Fruit> p2 = new Plate<>(new Fruit());

		// 存入元素正常
		p2.set(new Fruit());
		p2.set(new Apple());

		// 读取出来的东西只能存放在Object类里。
		// Apple newFruit3 = p.get(); // Error
		// Fruit newFruit1 = p.get(); // Error
		Object newFruitget = p2.get();
		System.out.println(newFruitget);
	}

}
