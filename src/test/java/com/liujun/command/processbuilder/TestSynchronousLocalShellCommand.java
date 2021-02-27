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

  /** 异步执行命令 */
  @Test
  public void synchornousDoCommand() {
    this.runCommand("ipconfig /all");
    this.runCommand("ping www.baidu.com");
    this.runCommand("abcdef");
  }

  private void runCommand(String commandStr) {
    List<String> commandList = Arrays.asList("cmd.exe", "/c", commandStr);
    SynchronousLocalShellCommand command = new SynchronousLocalShellCommand(commandList);

    String commandRsp = command.doCommand();
    Assert.assertNotNull(commandRsp);
    System.out.println(commandRsp);
  }
}
