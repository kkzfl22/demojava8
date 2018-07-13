package com.demo.effectivejava.thirty.enums.bad;

/**
 * doc:特定于常的方法实现有一个美中不足的地方，它们使得在枚举常量中共享代码变得更加困难了。
 * 
 * 1，例如：考虑用一个表示薪资包中的工作天数。这个枚举有一个方法，根据给某工作的基本我资以及当天的工作时音，来计算他当天的报酬。
 * 
 * 在5 个工作日中，用switch语句，很容易通过多个case标签分别应用到两个代码片断中，来完成这一计算。
 * 
 * 为了简洁起见，这个示例中的代码使用了double,但是注意double并不是适合薪资应用程序的数据类型
 * 
 * 
 * 代码非常的简洁，但从维护的角度来看，非常危险。假设将一个元素添加到该枚举中或许是一个表示假期天数的特殊值。但是忘记给switch添加相应的case.程序依然可以编译。但pay方法会悄悄地将将假期的工资计算成与正常日的相同
 * 
 *
 * @author liujun
 * @date 2018/07/12
 */
public enum PayrollDay {

	MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

	private static final int HOURS_PER_SHIFT = 8;

	double pay(double hoursWorked, double payRate) {
		double basePay = hoursWorked * payRate;

		double overtimePay;

		switch (this) {
		case SATURDAY:
		case SUNDAY:
			overtimePay = hoursWorked * payRate / 2;
		default:
			overtimePay = hoursWorked <= HOURS_PER_SHIFT ? 0 : (hoursWorked - HOURS_PER_SHIFT) * payRate / 2;
		}

		return basePay + overtimePay;
	}

}
