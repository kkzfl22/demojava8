package com.liujun.cycledepend.run;

import java.util.List;

import com.liujun.cycledepend.pkgitem.HealthRecord;
import com.liujun.cycledepend.pkgother.HealthTask;

public class RunHealTask {
	
	public static void main(String[] args) {
		
		HealthRecord record = new HealthRecord();
		record.addTask("跑步", 10);
		record.addTask("吃早饭", 20);
		
		List<HealthTask> tasks = record.getTasks();
		
		for (HealthTask healthTask : tasks) {
			int countvalue =  healthTask.calculatePointForTask();
			System.out.println("cout data:"+countvalue);
		}
		
	}

}
