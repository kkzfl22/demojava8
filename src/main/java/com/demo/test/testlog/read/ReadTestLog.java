package com.demo.test.testlog.read;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ReadTestLog {

	enum ContEnum {
		
		START("-start-"),

		THREAD_NUM("threadNum:"),

		QUALITY_NUM("quality:"),

		STARTTIME("starttime:"),

		ENDTIME("endtime:"),
		
		OVER("-over-")
		;

		private String key;

		private ContEnum(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
		
	};

	/**
	 * 读取集合信息
	 * 
	 * @param filePath
	 * @return
	 */
	public List<LogFileInfoBo> readList(String filePath) {
		List<LogFileInfoBo> resultList = null;

		FileReader reader = null;
		BufferedReader buffer = null;
		try {
			reader = new FileReader(filePath);
			buffer = new BufferedReader(reader);
			
			String line = null;
			
			while((line = buffer.readLine()) != null)
			{
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}

		return resultList;
	}

}
