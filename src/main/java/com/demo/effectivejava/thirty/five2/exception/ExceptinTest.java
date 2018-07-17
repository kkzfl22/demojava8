 package com.demo.effectivejava.thirty.five2.exception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
 public @interface ExceptinTest {
	
	Class<? extends Exception> value();
}
