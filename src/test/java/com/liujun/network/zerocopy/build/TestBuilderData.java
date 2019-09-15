package com.liujun.network.zerocopy.build;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * 测试数据构建
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/10
 */
public class TestBuilderData {

  @Test
  public void testBuilder() {

    String basePath = "D:\\java\\test\\randomdata\\";

    this.builderFile(basePath + "bigdata_1M.data", 1 * 1024 * 1024);
    this.builderFile(basePath + "bigdata_2M.data", 2 * 1024 * 1024);
    this.builderFile(basePath + "bigdata_4M.data", 4 * 1024 * 1024);
    this.builderFile(basePath + "bigdata_6M.data", 6 * 1024 * 1024);
    this.builderFile(basePath + "bigdata_8M.data", 8 * 1024 * 1024);
    this.builderFile(basePath + "bigdata_16M.data", 16 * 1024 * 1024);
    this.builderFile(basePath + "bigdata_32M.data", 32 * 1024 * 1024);
    this.builderFile(basePath + "bigdata_64M.data", 64 * 1024 * 1024);
    this.builderFile(basePath + "bigdata_256M.data", 256 * 1024 * 1024);
    this.builderFile(basePath + "bigdata_512M.data", 512 * 1024 * 1024);
    this.builderFile(basePath + "bigdata_1G.data", 1024l * 1024 * 1024);
  }

  /**
   * 进行构建文件
   *
   * @param path 路径
   * @param fileSize 文件大小
   */
  private void builderFile(String path, long fileSize) {
    BuilderDataInf builder = new BuilderDataNumberImpl();
    builder.builderData(path, fileSize);

    File checkFile = new File(path);

    Assert.assertEquals(true, checkFile.exists());
    Assert.assertEquals(fileSize, checkFile.length());
  }
}
