 package com.demo.effectivejava.thirty.five2.exception2;

import java.util.ArrayList;
import java.util.List;

public class Sample3 {
	 
	@ExceptionTests({IndexOutOfBoundsException.class,NullPointerException.class})
	 public static void doublyBad() {
		 List<String> list = new ArrayList<>();
		 
		 list.addAll(5, null);
	 }

}
