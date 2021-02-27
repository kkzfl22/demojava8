package com.liujun.command;

import com.liujun.command.runtime.AsynchronousLocalShellCommand;
import com.liujun.command.runtime.SynchronousLocalShellCommand;
import com.liujun.command.threadpool.TaskThreadDataPool;
import com.utils.ThreadUtils;

import java.io.IOException;

/**
 * 测试linux下命令的运行Runtime方式的运行
 *
 * @author liujun
 * @version 0.0.1
 */
public class RuntimeLinuxMain {

  public static void main(String[] args) {
    RuntimeLinuxMain instance = new RuntimeLinuxMain();
    // 异步的执行
    instance.asynchronousDoCommand();
    // 同步的执行
    // instance.synchornousDoCommand();
  }

  /** 异步执行命令 */
  private void asynchronousDoCommand() {

    // 执行一个正常的命令,一直会执行
    this.runAsyncCommand("ping www.baidu.com");
    // 运行一个脚本文件
    this.runAsyncCommand("/home/liujun/datarun/shell/run.sh");
    // 执行一个错误命令
    this.runAsyncCommand("adfadsfa");
    // 停止线程池
    TaskThreadDataPool.INSTANCE.shutdown();
  }

  /**
   * 异步的执行操作
   *
   * @param commandStr
   */
  private void runAsyncCommand(String commandStr) {
    AsynchronousLocalShellCommand command = new AsynchronousLocalShellCommand(commandStr);
    boolean submitFlag = false;
    try {
      command.doCommand();
      submitFlag = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    // 当提交失败时，不再继续获取数据
    if (!submitFlag) {
      return;
    }
    for (int i = 0; i < 5; i++) {
      int code = command.rspProcess();
      // 获取正常的结果
      String successData = command.getSuccessData();
      // 获取错误的信息
      String errorData = command.getErrorData();
      System.out.println(
          "当前第:" + i + "次，code:" + code + "，成功结果:" + successData + ",失败结果:" + errorData);
      // 由于这个ping执行需要一定的时间每次休眠1秒
      ThreadUtils.sleep(1);
    }

    // 停止运行
    command.stop();
    System.out.println("结束---------------");
  }

  /** 同步执行命令 */
  private void synchornousDoCommand() {
    // 执行一个ping命令，将在5次后返回
    this.runSyncCommand("ping -c 5 www.baidu.com");
    // 执行一个shell脚本
    this.runSyncCommand("/home/liujun/datarun/shell/run.sh");
    // 执行一个错误的命令，不存在的
    this.runSyncCommand("adfadsfa");
  }

  /**
   * 执行同步的命令操作
   *
   * @param commandStr
   */
  private void runSyncCommand(String commandStr) {
    SynchronousLocalShellCommand command = new SynchronousLocalShellCommand(commandStr);
    String commandRsp = command.doCommand();
    System.out.println("同步执行结果:" + commandRsp);
    System.out.println("结束---------------");
  }
}
