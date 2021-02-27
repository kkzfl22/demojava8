package com.liujun.command.runtime;

import com.liujun.command.threadpool.TaskThreadDataPool;
import com.utils.ThreadUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * 测试异步命令执行
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestAsynchronousLocalShellCommand {

  /** 测试异步命令的执行 */
  @Test
  public void asynchronous() throws IOException {

    // ping命令在windows上执行三次后会正常退出
    this.runCommand("ping www.baidu.com");
    // 执行一个window脚本
    this.runCommand("D:/run/bat/run.bat");
    // 执行一个错误命令
    this.runCommand("adfadsfa");

    // 停止线程池
    TaskThreadDataPool.INSTANCE.shutdown();
  }

  /**
   * 命令的执行操作
   *
   * @param commandStr
   */
  private void runCommand(String commandStr) throws IOException {
    // 进行命令的执行操作
    AsynchronousLocalShellCommand command = new AsynchronousLocalShellCommand(commandStr);
    // 开始执行命令
    command.doCommand();

    for (int i = 0; i < 3; i++) {
      int code = command.rspProcess();
      String outData = command.getSuccessData();
      String errorMsg = command.getErrorData();
      System.out.println(
          "第" + i + "次，errorcode :" + code + ",成功信息:" + outData + ",errorData:" + errorMsg);
      ThreadUtils.sleep(1);
    }

    // 不再运行
    command.stop();
    System.out.println("结束----------");
  }
}
