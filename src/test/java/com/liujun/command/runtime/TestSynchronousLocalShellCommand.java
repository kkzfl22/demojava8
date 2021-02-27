package com.liujun.command.runtime;

import org.junit.Assert;
import org.junit.Test;

/**
 * 测试同步命令的执行
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestSynchronousLocalShellCommand {

  /** 同步执行命令 */
  @Test
  public void synchronousDoCommand() {
    // ping命令在windows上执行三次后会正常退出
    this.runCommand("ping www.baidu.com");
    // 执行一个window脚本
    this.runCommand("D:/run/bat/run.bat");
    // 执行一个错误命令
    this.runCommand("adfadsfa");
  }

  private void runCommand(String commandStr) {
    SynchronousLocalShellCommand command = new SynchronousLocalShellCommand(commandStr);
    String commandRsp = command.doCommand();
    Assert.assertNotNull(commandRsp);
    System.out.println("命令:" + commandStr + ",执行结果:" + commandRsp);
    System.out.println("结束----------");
  }
}
