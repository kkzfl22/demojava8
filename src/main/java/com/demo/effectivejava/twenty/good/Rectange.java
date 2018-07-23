package com.demo.effectivejava.twenty.good;

/**
 * 矩形
 * 
 * @author liujun
 * @date 2018/07/05
 */
public class Rectange extends AbsFigure {

	final double length;

	final double width;

	public Rectange(double length, double width) {
		this.length = length;
		this.width = width;
	}

	@Override
	double area() {
		return length * width;
	}

}
