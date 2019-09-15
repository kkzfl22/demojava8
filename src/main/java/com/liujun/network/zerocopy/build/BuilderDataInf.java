package com.liujun.network.zerocopy.build;

/**
 * 生成数据的接口
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/10
 */
public interface BuilderDataInf {

  /**
   * 生成文件数据的接口
   *
   * @param path 生成文件的路径
   * @param maxFileSize 生成文件的大小
   */
  void builderData(String path, long maxFileSize);
}
