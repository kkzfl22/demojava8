package com.demo.effectivejava.seven.thirtynine.one;

import java.util.Date;

public class TestPeriod {

	public static void main(String[] args) {
		Date start = new Date();
		
		Date end = new Date();
		
		Period p = new Period(start, end);
		//此存在可变
		end.setYear(78);
		
	}
}
