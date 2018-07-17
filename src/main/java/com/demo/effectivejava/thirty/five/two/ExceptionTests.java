package com.demo.effectivejava.thirty.five.two;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * doc:针对抛出特殊异常时才成功的测试添加支持
 * 
 * @author liujun
 * @date 2018/07/16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTests {

	Class<? extends Exception> value();

}
