package com.liujun.command.processbuilder;

import com.constant.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
  private final List<String> command;

  public SynchronousLocalShellCommand(List<String> command) {
    this.command = command;
  }

  /**
   * 执行命令并返回结果
   *
   * @return 命令执行结果
   */
  public String doCommand() {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      // 将错误输出流转移到标准输出流中,但使用Runtime不可以
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();
      String dataMsg = reader(process.getInputStream());
      int rsp = process.waitFor();
      logger.info("run command {}, response {}", command, rsp);
      return dataMsg;
    } catch (IOException | InterruptedException e) {
      logger.error("command : {} ,exception", command, e);
    }

    return null;
  }

  /**
   * 数据读取操作
   *
   * @param input 输入流
   */
  private String reader(InputStream input) {
    StringBuilder outDat = new StringBuilder();
    try (InputStreamReader inputReader = new InputStreamReader(input, StandardCharsets.UTF_8);
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
