package com.liujun.command.runtime;

import com.constant.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 同步调用java进行本地的shell脚本的执行,并且为并行执行脚本的输出操作
 *
 * <p>按类为例示
 *
 * @author liujun
 * @version 0.0.1
 */
public class SynchronousLocalShellCommand {

  private Logger logger = LoggerFactory.getLogger(SynchronousLocalShellCommand.class);

  /** 命令信息 */
  private final String command;

  public SynchronousLocalShellCommand(String command) {
    this.command = command;
  }

  /**
   * 执行命令并返回结果
   *
   * @return 命令执行结果
   */
  public String doCommand() {

    // 获取操作流
    Process process = null;
    String errorMsg = null;
    try {
      process = Runtime.getRuntime().exec(command);
    } catch (IOException e) {
      logger.error("command : {} ,exception", command, e);
      errorMsg = e.getMessage();
    }

    // 当对象获取不成功时，返回对象
    if (null == process) {
      return errorMsg;
    }

    // 获取子进程的输入流。输入流获得由该 Process 对象表示的进程的标准输出流。
    String commandRspOk = reader(process.getInputStream());
    // 进行错误信息的读取操作
    String commandRspError = reader(process.getErrorStream());

    // 构建响应结果
    String commandRsp = commandRspOk + Symbol.LINE + Symbol.LINE + commandRspError;

    int rsp = -1;
    try {
      // 等待进程结束。
      rsp = process.waitFor();
    } catch (InterruptedException e) {
      logger.error("run command {} InterruptedException", command, rsp);
    }

    logger.info("run command {}, response {}", command, rsp);
    return commandRsp;
  }

  /**
   * 数据读取操作
   *
   * @param input 输入流
   */
  private String reader(InputStream input) {
    StringBuilder outDat = new StringBuilder();
    try (InputStreamReader inputReader = new InputStreamReader(input, Charset.forName("GBK"));
        // try (InputStreamReader inputReader = new InputStreamReader(input,
        // StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputReader)) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        outDat.append(line);
        outDat.append(Symbol.LINE);
      }
    } catch (IOException e) {
      logger.error("command : {} ,exception", command, e);
    }
    return outDat.toString();
  }
}
