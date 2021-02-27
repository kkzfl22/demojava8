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
    // 执行一个正常的命令，带终止
    this.runCommand("ping www.baidu.com -t");

    // 执行一个错误命令
    this.runCommand("adfadsfa");

    // 运行一个脚本文件
    String path = this.getClass().getClassLoader().getResource("./command").getPath();
    path = path + "/run.bat";
    this.runCommand(path.substring(1));

    TaskThreadDataPool.INSTANCE.shutdown();
  }

  private void runCommand(String commandStr) {
    List<String> commandList = Arrays.asList("cmd.exe", "/c", commandStr);
    AsynchronousLocalShellCommand command = new AsynchronousLocalShellCommand(commandList);
    for (int i = 0; i < 3; i++) {
      int code = command.rspProcess();
      String outData = command.getOutDat();
      Assert.assertNotNull("code:" + code + ",rspmsg:" + outData);
      System.out.println(outData);
      ThreadUtils.sleep(1);
    }

    // 不再运行
    command.stop();

    int code = command.rspProcess();
    String outData = command.getOutDat();

    System.out.println("结果状态：" + code + "，执行结果:" + outData);
  }
}
