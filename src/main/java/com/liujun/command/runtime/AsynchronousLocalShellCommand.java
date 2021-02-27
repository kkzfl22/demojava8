package com.liujun.command.runtime;

import com.constant.Symbol;
import com.liujun.command.threadpool.TaskThreadDataPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
  private final String command;

  /** 命令执行的工作进程 */
  private Process process;

  /** 数据的输出流 */
  private InputStream input;

  /** 错误的输出流 */
  private InputStream error;

  /** 成功执行输出信息 */
  private final StringBuilder successData = new StringBuilder();

  /** 失败时输出的信息 */
  private final StringBuilder errorData = new StringBuilder();

  /** 进程执行结束后的结果,-1 初始化，0，成功执行结束 */
  private AtomicInteger processOutCode = new AtomicInteger(-1);

  /** 运行标识 */
  private AtomicBoolean runFlag = new AtomicBoolean(true);

  public AsynchronousLocalShellCommand(String command) {
    this.command = command;
  }

  /** 开始执行命令 */
  public void doCommand() throws IOException {
    // 获取操作流
    this.process = Runtime.getRuntime().exec(command);

    if (null != process) {
      // 获得由该 Process 对象表示的进程的标准输出流。
      input = process.getInputStream();
      // 如果正常流获取不到，则获取错误的输出
      error = process.getErrorStream();

      // 成功时才将任务提交线程池运行
      TaskThreadDataPool.INSTANCE.submit(this);
    }
  }

  @Override
  public void run() {
    // 读取input中的数据，记录到successData这个StringBuilder对象中
    reader(this.input, successData);
    // 错误的数据流读取操作，记录到errorData这个StringBuilder对象中
    reader(this.error, errorData);

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
  public String getSuccessData() {
    return successData.toString();
  }

  /**
   * 获取错误的信息
   *
   * @return
   */
  public String getErrorData() {
    return errorData.toString();
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
    if (null != process) {
      // 停止子进程
      process.destroy();
    }
  }

  /**
   * 数据读取操作
   *
   * @param input 输入流
   */
  private void reader(InputStream input, StringBuilder data) {
    // try (InputStreamReader inputReader = new InputStreamReader(input, StandardCharsets.UTF_8);
    try (InputStreamReader inputReader = new InputStreamReader(input, Charset.forName("GBK"));
        BufferedReader bufferedReader = new BufferedReader(inputReader)) {
      String line;
      while ((line = bufferedReader.readLine()) != null && runFlag.get()) {
        data.append(line);
        data.append(Symbol.LINE);
      }
    } catch (IOException e) {
      logger.error("command : {} ,exception", command, e);
    }
  }
}
