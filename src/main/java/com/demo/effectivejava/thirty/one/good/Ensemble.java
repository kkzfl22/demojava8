package com.demo.effectivejava.thirty.one.good;

/**
 * doc:可以解决之前的问题，永远不要根据 枚举的充数展出与它关联的值 ，而是要将它保存在一个实例域中
 * 
 * @author liujun
 * @date 2018/07/13
 */
public enum Ensemble {

	SOLE(1),

	DUET(2),

	TRIO(3),

	QUARTET(4),

	QUINTET(5),

	SEXTET(6),

	SEPTET(7),

	OCTET(8),

	DOUBLE_QUARTET(8),

	NONET(9),

	DECTET(10),

	TRIPLE_QUARTET(12);

	private final int numberOfMuciesians;

	Ensemble(int size) {
		this.numberOfMuciesians = size;
	}

	public int numberOfMusicians() {
		return numberOfMuciesians;
	}

}
