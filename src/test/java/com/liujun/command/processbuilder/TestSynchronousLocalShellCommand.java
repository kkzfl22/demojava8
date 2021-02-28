package com.liujun.command.processbuilder;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 测试同步命令的执行
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestSynchronousLocalShellCommand {

  /** 同步执行命令 */
  @Test
  public void synchornousDoCommand() {
    // 运行一个正常的命令
    this.runCommand("ping www.baidu.com");
    // 运行一个bat脚本
    this.runCommand("D:/run/bat/run.bat");
    // 错误命令
    this.runCommand("abcdef");
  }

  /**
   * 运行command
   *
   * @param commandStr 错误命令行
   */
  private void runCommand(String commandStr) {
    List<String> commandList = Arrays.asList("cmd.exe","/C",commandStr);
    SynchronousLocalShellCommand command = new SynchronousLocalShellCommand(commandList);

    String commandRsp = command.doCommand();
    Assert.assertNotNull(commandRsp);
    System.out.println(commandRsp);
  }
}
