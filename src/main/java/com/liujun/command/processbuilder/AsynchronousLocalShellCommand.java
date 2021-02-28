package com.liujun.command.processbuilder;

import com.constant.Symbol;
import com.liujun.command.threadpool.TaskThreadDataPool;
import com.liujun.thread.threadpool.TaskThreadDataPoolDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步调用java进行本地的shell脚本的执行,并且为并行执行脚本的输出操作
 *
 * <p>按类为例示
 *
 * @author liujun
 * @version 0.0.1
 */
public class AsynchronousLocalShellCommand implements Runnable {

  private Logger logger = LoggerFactory.getLogger(AsynchronousLocalShellCommand.class);

  /** 命令信息 */
  private final List<String> command;

  /** 运行的处理流程 */
  private Process process;

  /** 数据的输出流 */
  private InputStream input;

  /** 输出信息 */
  private final StringBuilder outDat = new StringBuilder();

  /** 进程执行结束后的结果,-1 初始化，0，成功执行结束 */
  private AtomicInteger processOutCode = new AtomicInteger(-1);

  /** 运行标识 */
  private AtomicBoolean runFlag = new AtomicBoolean(true);

  public AsynchronousLocalShellCommand(List<String> command) {
    this.command = command;
  }

  /** 开始执行命令 */
  public void doCommand() {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      // 将错误输出流转移到标准输出流中,但使用Runtime不可以
      processBuilder.redirectErrorStream(true);
      this.process = processBuilder.start();
    } catch (IOException e) {
      logger.error("command : {} ,exception", command, e);
    }

    // 获取子进程的输入流。输入流获得由该 Process 对象表示的进程的标准输出流。
    input = process.getInputStream();

    // 成功时才将任务提交线程池运行
    TaskThreadDataPool.INSTANCE.submit(this);
  }

  @Override
  public void run() {
    // 进行数据读取操作
    reader(this.input);

    int rsp = -1;
    try {
      // 等待进程结束，不能直接调用exitValue() ，这将导致还没有运行完成就返回了结果
      rsp = process.waitFor();
    } catch (InterruptedException e) {
      logger.error("run command {} InterruptedException", command, rsp);
      // 停止操作
      rsp = -5;
    }

    processOutCode.set(rsp);
    logger.info("run command {}, response {}", command, rsp);
  }

  /**
   * 获取执行结果
   *
   * @return
   */
  public String getOutDat() {
    return outDat.toString();
  }

  /**
   * 返回进程执行的结果码
   *
   * @return
   */
  public int rspProcess() {
    return processOutCode.get();
  }

  /**
   * 停止操作
   *
   * @param
   */
  public void stop() {
    // 停止读取输出操作
    this.runFlag.set(false);
    // 停止子进程
    process.destroy();
  }

  /**
   * 数据读取操作
   *
   * @param input 输入流
   */
  private void reader(InputStream input) {
    try (InputStreamReader inputReader = new InputStreamReader(input, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputReader)) {
      String line;
      while ((line = bufferedReader.readLine()) != null && runFlag.get()) {
        outDat.append(line);
        outDat.append(Symbol.LINE);
      }
    } catch (IOException e) {
      logger.error("command : {} ,exception", command, e);
    }
  }
}
