package com.liujun.command.processbuilder;

import com.liujun.command.threadpool.TaskThreadDataPool;
import com.utils.ThreadUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 测试异步命令执行
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestAsynchronousLocalShellCommand {

  /** 测试异步命令的执行 */
  @Test
  public void asynchronous() {
    // 运行一个正常的命令
    this.runCommand("ping www.baidu.com");
    // 运行一个bat脚本
    this.runCommand("D:/run/bat/run.bat");
    // 错误命令
    this.runCommand("abcdef");

    TaskThreadDataPool.INSTANCE.shutdown();
  }

  private void runCommand(String commandStr) {
    // List<String> commandList = Arrays.asList("cmd.exe", "/c", commandStr);
    List<String> commandList = Arrays.asList(commandStr);
    AsynchronousLocalShellCommand command = new AsynchronousLocalShellCommand(commandList);
    command.doCommand();
    for (int i = 0; i < 3; i++) {
      int code = command.rspProcess();
      String outData = command.getOutDat();
      Assert.assertNotNull(outData);
      System.out.println("第" + i + ",结果码,code:" + code + ",rspmsg:" + outData);
      ThreadUtils.sleep(1);
    }

    // 不再运行
    command.stop();

    int code = command.rspProcess();
    String outData = command.getOutDat();

    System.out.println("命令" + commandStr + ",结果码,code:" + code + ",rspmsg:" + outData);
    System.out.println("结束----------");
  }
}
