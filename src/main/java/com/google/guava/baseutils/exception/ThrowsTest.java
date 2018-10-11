package com.google.guava.baseutils.exception;

import java.sql.BatchUpdateException;
import java.sql.SQLException;

import org.junit.Test;

import com.google.common.base.Throwables;

public class ThrowsTest {

	@Test
	public void throwTest() {
		int x = 0;

		try {
			if (x == 0) {
				throwsNew2();
			}
		} catch (Throwable e) {
			// 找到问题发生的根源位置抛出的异常信息
			Throwable roor = Throwables.getRootCause(e);
			System.out.println(roor);
		}
	}

	public void throwsNew() throws BatchUpdateException {
		throw new BatchUpdateException("throw batchException", new int[] { 1, 2 });
	}

	public void throwsNew2() throws SQLException {
		try {
			throwsNew();
		} catch (BatchUpdateException e) {
			throw new SQLException("sqlException", e);
		}

	}
}
