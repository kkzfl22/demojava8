package com.demo.effectivejava.thirty.two.good;

import java.util.EnumSet;
import java.util.Set;

public class Text {

	public enum Style {
		BOLD, ITALIC, UNDERLINE, STRIKETHROUGH
	}

	public static void applyStyle(Set<Style> style) {

	}

	public static void main(String[] args) {
		Text.applyStyle(EnumSet.of(Style.BOLD, Style.ITALIC));
	}

}
