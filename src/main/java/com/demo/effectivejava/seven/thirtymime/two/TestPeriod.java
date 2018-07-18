package com.demo.effectivejava.seven.thirtymime.two;

import java.util.Date;

public class TestPeriod {

	public static void main(String[] args) {
		Date start = new Date();
		
		Date end = new Date();
		
		Period p = new Period(start, end);
		
		p.getEnd().setYear(78);
		
		System.out.println(p.getEnd());
		
	}
}
