package com.demo.effectivejava.seven.thirtynine.two;

import java.util.Date;

public class TestPeriod {

	public static void main(String[] args) {
		Date start = new Date();
		
		Date end = new Date();
		
		Period p = new Period(start, end);
		
		//此还存在可以对对象的修改
		p.getEnd().setYear(78);
		
		System.out.println(p.getEnd());
		
	}
}
