package com.demo.effectivejava.twenty;

/**
 * d:标签类的样低码，非常不好的实践 用来与最佳实践形成对比
 * 
 * 标签类中有许多的缺点：
 * 
 * 1，充斥各种样板代码，包括枚举声明、标签域、以及条件语句
 * 
 * 2,由于多个实现乱七入糟的挤在单个类中，破坏了可读取性
 * 
 * 3，内存占用也增加了，因为实例承担着属于其他风格的不相相关的域。
 * 
 * 4，域不能做final，随便非构造器初始不了相关的域，产生更多的样板代码。
 * 
 * 5，构造器必须不借助编译器，来设置标签域，并初始化正确的数据域：如果初始化了错误的域，程序就会运行失败。
 * 
 * 6，无法给标签类添加风格，除非可以修改它的源码。
 * 
 * 7，如果一定要添加风格，就必须记得给每个条件语句都添加一个条件，否则类就会在运行时失败，
 * 
 * 总结： 标签类过于冗长，容易出错，并且效率低下
 * 
 * @author liujun
 * @date 2018/07/05
 */
public class Figure {

	enum Shape {
		RECTANGLE, CIRCLE
	};

	final Shape shape;

	double length;

	double width;

	double radius;

	public Figure(double length, double width) {
		shape = Shape.RECTANGLE;
		this.length = length;
		this.width = width;
	}

	double area() {
		switch (shape) {
		case RECTANGLE:
			return length * width;
		case CIRCLE:
			return Math.PI * (radius * radius);
		default:
			throw new AssertionError();
		}
	}
}
