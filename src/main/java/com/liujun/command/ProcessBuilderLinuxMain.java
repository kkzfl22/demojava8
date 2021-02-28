package com.liujun.command;

import com.liujun.command.processbuilder.AsynchronousLocalShellCommand;
import com.liujun.command.processbuilder.SynchronousLocalShellCommand;
import com.liujun.command.threadpool.TaskThreadDataPool;
import com.utils.ThreadUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 测试linux下命令的运行processbuilder方式的运行
 *
 * @author liujun
 * @version 0.0.1
 */
public class ProcessBuilderLinuxMain {

  public static void main(String[] args) {
    ProcessBuilderLinuxMain instance = new ProcessBuilderLinuxMain();
    // 异步的执行
    instance.asynchronousDoCommand();
    // 同步的执行
    // instance.synchornousDoCommand();
  }

  /** 异步执行命令 */
  private void asynchronousDoCommand() {
    // 执行一个正常的命令，带终止
    this.runAsyncCommand("ping www.baidu.com");
    // 运行一个脚本文件
    this.runAsyncCommand("/home/liujun/datarun/shell/run.sh");
    // 执行一个错误命令
    this.runAsyncCommand("adfadsfa");
    TaskThreadDataPool.INSTANCE.shutdown();
  }

  /**
   * 异步的执行操作
   *
   * @param commandStr
   */
  private void runAsyncCommand(String commandStr) {
    List<String> commandList = Arrays.asList("bash", "-c", commandStr);
    AsynchronousLocalShellCommand command = new AsynchronousLocalShellCommand(commandList);
    command.doCommand();
    for (int i = 0; i < 3; i++) {
      int code = command.rspProcess();
      String outData = command.getOutDat();
      System.out.println("当前第:" + i + "次，code:" + code + "，响应结果 :" + outData);
      ThreadUtils.sleep(1);
    }

    // 不再运行
    command.stop();

    int code = command.rspProcess();
    String outData = command.getOutDat();

    System.out.println("结果状态：" + code + "，执行结果:" + outData);
    System.out.println("结束--------");
  }

  /** 同步执行命令 */
  private void synchornousDoCommand() {
    this.runSyncCommand("ping -c 5 www.baidu.com");
    this.runSyncCommand("/home/liujun/datarun/shell/run.sh");
    this.runSyncCommand("adfadsfa");
  }

  /**
   * 执行同步的命令操作
   *
   * @param commandStr
   */
  private void runSyncCommand(String commandStr) {
    List<String> commandList = Arrays.asList("bash", "-c", commandStr);
    SynchronousLocalShellCommand command = new SynchronousLocalShellCommand(commandList);
    String commandRsp = command.doCommand();
    System.out.println("同步执行结果:" + commandRsp);
    System.out.println("结束---------------");
  }
}
