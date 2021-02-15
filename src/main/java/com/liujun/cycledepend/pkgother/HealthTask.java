package com.liujun.cycledepend.pkgother;

import com.liujun.cycledepend.pkgitem.HealthRecord;

/**
 * 健康任务的类
 *
 * @author liujun
 * @version 0.0.1
 */
public class HealthTask {

  /** 健康等级 */
  private HealthRecord record;

  /** 任务 */
  private String taskName;

  /** 初始任务数 */
  private Integer initialHealthPoint;

  public HealthTask(HealthRecord record, String taskName, Integer initialHealthPoint) {
    this.record = record;
    this.taskName = taskName;
    this.initialHealthPoint = initialHealthPoint;
  }

  /**
   * 结算积分
   *
   * @return
   */
  public Integer calculatePointForTask() {
    // 计算该任务所能获取的积分需要的等级信息
    // 等级越低，积分越高，以鼓励多做任务
    Integer healthPointFormatHealthLevel = 12 / record.getHealthLevel();

    return initialHealthPoint + healthPointFormatHealthLevel;
  }

  public String getTaskName() {
    return taskName;
  }

  public Integer getInitialHealthPoint() {
    return initialHealthPoint;
  }
}
