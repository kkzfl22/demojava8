package com.demo.effectivejava.fifteen;

/**
 * doc：不可变对象的示例,照片数
 * 
 * @author liujun
 * @date 2018/07/04
 */
public final class Complex {

	/**
	 * 
	 */
	private final double re;

	/**
	 * 计算操作数2
	 */
	private final double im;

	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}

	public double realpart() {
		return re;
	}

	public double imaginaryPart() {
		return im;
	}

	public Complex add(Complex c) {
		return new Complex(re + c.re, im + c.im);
	}

	public Complex subtract(Complex c) {
		return new Complex(re - c.re, im - c.im);
	}

	public Complex multiply(Complex c) {
		return new Complex(re * c.re - im * c.im,
				// 输出
				re * c.im + im * c.re);
	}

	public Complex divide(Complex c) {
		double tem = c.re + c.im * c.im;
		return new Complex((re * c.re + im * c.im) / tem,
				// 输出
				(im * c.re - re * c.im) / tem);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(im);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(re);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Complex other = (Complex) obj;
		if (Double.doubleToLongBits(im) != Double.doubleToLongBits(other.im))
			return false;
		if (Double.doubleToLongBits(re) != Double.doubleToLongBits(other.re))
			return false;
		return true;
	}

	public int hashDouble(double val) {
		long longBits = Double.doubleToLongBits(re);
		return (int) (longBits ^ longBits >>> 32);
	}

	@Override
	public String toString() {
		return "Complex [re=" + re + ", im=" + im + "]";
	}

}
