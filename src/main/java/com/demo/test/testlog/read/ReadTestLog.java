package com.demo.test.testlog.read;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadTestLog {

	enum ContEnum {

		START("-start-"),

		THREAD_NUM("threadNum:"),

		QUALITY_NUM("quality:"),

		STARTTIME("starttime:"),

		ENDTIME("endtime:"),

		OVER("-over-");

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
		List<LogFileInfoBo> resultList = new ArrayList<>();

		FileReader reader = null;
		BufferedReader buffer = null;
		try {
			reader = new FileReader(filePath);
			buffer = new BufferedReader(reader);

			String line = null;

			LogFileInfoBo bo = null;
			RunTimeBo runTime = null;

			int index = 0;

			while ((line = buffer.readLine()) != null) {
				if (line.indexOf(ContEnum.START.getKey()) != -1) {
					bo = new LogFileInfoBo();
					continue;
				}
				// 读取线程数
				else if ((index = line.indexOf(ContEnum.THREAD_NUM.getKey())) != -1) {
					String threaNum = line.substring(index + ContEnum.THREAD_NUM.getKey().length()).trim();
					bo.setThreaNum(Integer.parseInt(threaNum));
				}
				// 质量参数
				else if ((index = line.indexOf(ContEnum.QUALITY_NUM.getKey())) != -1) {
					String qualityNum = line.substring(index + ContEnum.QUALITY_NUM.getKey().length()).trim();
					bo.setQuality(Integer.parseInt(qualityNum));
				}
				// 开始时间
				else if ((index = line.indexOf(ContEnum.STARTTIME.getKey())) != -1) {
					runTime = new RunTimeBo();
					String statTime = line.substring(index + ContEnum.STARTTIME.getKey().length()).trim();
					runTime.setStartTime(statTime);
				}

				// 开始时间
				else if ((index = line.indexOf(ContEnum.ENDTIME.getKey())) != -1) {
					String endTime = line.substring(index + ContEnum.ENDTIME.getKey().length()).trim();
					runTime.setEndTime(endTime);
					bo.addRunTimeList(runTime);
				}
				// 结束标识
				else if ((index = line.indexOf(ContEnum.OVER.getKey())) != -1) {
					resultList.add(bo);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resultList;
	}

	public void outExcelPrint(List<LogFileInfoBo> list) {
		if (null != list) {
			for (LogFileInfoBo item : list) {
				for (RunTimeBo run : item.getRunTimeList()) {
					System.out.println(item.getThreaNum() + " 	" + item.getQuality() + " 	" + run.getStartTime()
							+ " 	" + run.getEndTime());
				}
			}
		}
	}

	public static void main(String[] args) {
		ReadTestLog instance = new ReadTestLog();
		String filePath = "E:/test/km_jpgtozcc.log";
		List<LogFileInfoBo> list = instance.readList(filePath);
		instance.outExcelPrint(list);
		System.out.println(list.size());
	}

}
