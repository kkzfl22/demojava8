package com.liujun.network.zerocopy.zeroCopy;

import org.junit.Test;

/**
 * 测试零拷贝
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/14
 */
public class TestZeroCopy {
  @Test
  public void filecopy() {
    String src = "D:\\java\\test\\randomdata\\bigdata.data";
    String target = "./zipcopy.data";

    ZeroCopy.INSTANCE.fileCopy(src, target);
  }
}
