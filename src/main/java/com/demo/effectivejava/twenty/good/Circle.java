package com.demo.effectivejava.twenty.good;

/**
 * 圆形
 * 
 * @author liujun
 * @date 2018/07/05
 */
public class Circle extends AbsFigure {

	/**
	 * 圆的半径
	 */
	final double radius;

	public Circle(double radius) {
		this.radius = radius;
	}

	@Override
	double area() {
		return Math.PI * (radius * radius);
	}

}
