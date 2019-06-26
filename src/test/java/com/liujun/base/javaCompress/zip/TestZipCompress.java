package com.liujun.base.javaCompress.zip;

import org.junit.Test;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2019/06/26
 */
public class TestZipCompress {

  @Test
  public void testCompresszip() {
    ZipCompress.INSTANCE.zipCompress(
        "D:/java/test/searchEngine/test", "D:/java/test/out.zip");
  }
}
