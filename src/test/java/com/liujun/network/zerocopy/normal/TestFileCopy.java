package com.liujun.network.zerocopy.normal;

import org.junit.Test;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/14
 */
public class TestFileCopy {

  @Test
  public void filecopy() {
    String src = "D:\\java\\test\\randomdata\\bigdata.data";
    String target = "./copy.data";

    FileCopy.INSTANCE.fileCopy(src, target);
  }
}
