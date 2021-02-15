package com.liujun.cycledepend.pkgitem;

import java.util.ArrayList;
import java.util.List;

import com.liujun.cycledepend.pkgother.HealthTask;

/**
 * 健康档案的类
 *
 * 用于演示两个代码类的相互引用的关系，将使用jdpend工具来检查依赖关系
 *
 * @author liujun
 * @version 0.0.1
 */
public class HealthRecord {

  private List<HealthTask> tasks = new ArrayList<>();

  /**
   * 获取健康等级
   *
   * @return
   */
  public Integer getHealthLevel() {
    // 根据健康任务数据来判断健康等级
    // 任务越多说明越不健康，健康越低
    if (tasks.size() > 5) {
      return 1;
    }

    if (tasks.size() < 2) {
      return 3;
    }

    return 2;
  }

  public void addTask(String taskName, Integer initialHealthPoint) {
    HealthTask task = new HealthTask(this, taskName, initialHealthPoint);
    tasks.add(task);
  }

  public List<HealthTask> getTasks() {
    return tasks;
  }
}
