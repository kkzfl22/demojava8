package com.demo.effectivejava.thirty.enums.good;

/**
 * doc:真正想要的就是每当添加一个枚举常量时，就强制选择一种加班报酬策略。幸运的是有一种很好的方法可以实现这一点。
 * 这种想法就是将加班工资计算移到一个私有的嵌套枚举中，将这个策略枚举的实例传到PayrollDay的枚举的构造器中。
 * 
 * 之后payrollDay枚举加班工资计算委托给策略枚举，payrollday中就不需要switch语句或者特定于常量的方法实现了，
 * 虽然这种模式switch语句那么简洁，但更加安全，也更加灵活
 * 
 * @author liujun
 * @date 2018/07/12
 */
public enum PayrollDay {

	/**
	 * 星期一的加班工资计算
	 */
	MONDAY(PayType.WEEBDAY),

	/**
	 * 星期二的加班工资计算
	 */
	TUESDAY(PayType.WEEBDAY),

	/**
	 * 周三的加班工资计算
	 */
	WEDESDAY(PayType.WEEBDAY),

	/**
	 * 周四的加班工资计算
	 */
	THURSDAY(PayType.WEEBDAY),

	/**
	 * 周五的加班工资计算
	 */
	FRIDAY(PayType.WEEBDAY),

	/**
	 * 周六的加班工资计算
	 */
	SATURDAY(PayType.WEEKEND),

	/**
	 * 星期天的加班工资计算
	 */
	SUNDAY(PayType.WEEKEND);

	/**
	 * 加班工资的类型信息
	 */
	private final PayType payType;

	PayrollDay(PayType payType) {
		this.payType = payType;
	}

	double pay(double hoursWorked, double payRate) {
		return payType.pay(hoursWorked, payRate);
	}

	private enum PayType {

		WEEBDAY {
			double overtimePay(double hours, double payRate) {
				return hours <= HOURS_PER_SHIFT ? 0 : (hours - HOURS_PER_SHIFT) * payRate / 2;
			}
		},

		WEEKEND {
			double overtimePay(double hours, double payRate) {
				return hours * payRate / 2;
			}
		};

		private static final int HOURS_PER_SHIFT = 8;

		abstract double overtimePay(double hrs, double payRate);

		double pay(double hoursWorked, double payRate) {
			double basePay = hoursWorked * payRate;
			return basePay + overtimePay(hoursWorked, payRate);
		}

	}
}
