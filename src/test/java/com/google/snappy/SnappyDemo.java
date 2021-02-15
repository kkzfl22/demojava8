package com.google.snappy;

import org.junit.Test;
import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * 使用google的snappy进行字符串的压缩与解压缩操作
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/10/11
 */
public class SnappyDemo {

  @Test
  public void strCompress() throws InterruptedException {
    String comp =
        "Snappy is a compression/decompression library. It does not aim for maximum compression, "
            + "or compatibility with any other compression library; instead, it aims "
            + "for very high speeds and reasonable compression. For instance, compared to "
            + "the fastest mode of zlib, Snappy is an order of magnitude faster for most inputs, "
            + "but the resulting compressed files are anywhere from 20% to 100% bigger."
            + " On a single core of a Core i7 processor in 64-bit mode, Snappy compresses at about "
            + "250 MB/sec or more and decompresses at about 500 MB/sec or more"
            + "In case there is some problem with hadoop cluster and the edits file is corrupted it "
            + "is possible to save at least part of the edits file that is correct. "
            + "This can be done by converting the binary edits to XML, edit it manually and then convert "
            + "it back to binary. The most common problem is that the edits file is missing the closing "
            + "record (record that has opCode -1). This should be recognized by the tool and the XML "
            + "format should be properly closed";

    try {
      byte[] value = Snappy.compress(comp);
      System.out.println("compress compress before:" + comp.getBytes().length);
      System.out.println("compress result:" + value.length);

      byte[] beforeValueBytes = Snappy.uncompress(value);

      System.out.println("compress before array :" + new String(beforeValueBytes));
      System.out.println("compress before array length:" + beforeValueBytes.length);

      
      Thread.sleep(100000);
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void strUnCompress() {}
}
