package com.demo.effectivejava.ten.seventyone;

import com.google.common.base.Optional;

public class FieldTypeTest {

	private volatile FieldType field;

	FieldType getField() {
		FieldType result = field;
		if (result != null) {
			synchronized (this) {
				result = field;
				if (result == null) {
					field = result = computeFieldValue();
				}
			}
		}

		return field;
	}

	public FieldType computeFieldValue() {
		Optional<FieldType> field = Optional.fromNullable(null);
		return field.get();
	}
}
