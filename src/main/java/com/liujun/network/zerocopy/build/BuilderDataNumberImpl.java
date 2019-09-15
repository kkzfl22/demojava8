package com.liujun.network.zerocopy.build;

import com.utils.IOUtils;

import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 构建测试数据
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/10
 */
public class BuilderDataNumberImpl implements BuilderDataInf {

  private static final long LINE_SIZE = 1000;

  /** 换行符 */
  private static final String LINE = "\n";

  /**
   * 构建数据
   *
   * @param path 写入文件的路径
   * @param maxFileSize 构建数据的大小
   */
  @Override
  public void builderData(String path, long maxFileSize) {

    FileOutputStream output = null;

    try {
      output = new FileOutputStream(path);

      long currFileSize = 0;
      byte[] outdata;

      while (currFileSize < maxFileSize) {
        long countWrite = this.countRunSize(currFileSize, maxFileSize);
        outdata = this.lineData(countWrite);
        output.write(outdata);
        currFileSize += outdata.length;
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.close(output);
    }
  }

  /**
   * 生成写入的数据
   *
   * @param write 写入的大小
   * @return 字符
   */
  public byte[] lineData(long write) {
    StringBuilder outData = new StringBuilder();
    for (int i = 0; i < write - 1; i++) {
      outData.append(ThreadLocalRandom.current().nextInt(1, 9));
    }
    outData.append(LINE);
    return outData.toString().getBytes();
  }

  /**
   * 计算运行的大小
   *
   * @param currSize 当前大小
   * @param maxFileSize 最大写入的大小
   * @return 最大写入文件大小
   */
  private long countRunSize(long currSize, long maxFileSize) {
    long needWriteData = maxFileSize - currSize;
    if (LINE_SIZE > needWriteData) {
      return needWriteData;
    } else {
      return LINE_SIZE;
    }
  }
}
