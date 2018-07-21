package com.demo.effectivejava.thirty.seven.demo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class Test {

	public static void main(String[] args) {
		TriskResult result = new TriskResult();

		FileOutputStream outStream = null;
		ObjectOutputStream objStream = null;

		try {
			outStream = new FileOutputStream("E:\\test\\out\\test.obj");
			objStream = new ObjectOutputStream(outStream);

			objStream.writeInt(12345);
			objStream.writeObject(result);
			objStream.writeObject(new Date());
			objStream.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != objStream) {
				try {
					objStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != outStream) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
