package com.demo.effectivejava.thirty.two.bad;

public class Text {

	public static final int STYLE_BOLD = 1 << 0;

	public static final int STYLE_ITALIC = 1 << 1;

	public static final int STYLE_UNDERLINE = 1 << 2;

	public static final int STYLE_STRIKETHROUGH = 1 << 3;

	public static void applyStyle(int style) {

	}
	
	
	/**
	 * 使用OR位运算将几个常量合并到一个集合中，称作为位域
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Text.applyStyle(STYLE_BOLD|STYLE_ITALIC);
	}
}
