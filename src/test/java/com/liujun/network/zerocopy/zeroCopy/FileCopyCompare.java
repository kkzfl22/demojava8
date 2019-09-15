package com.liujun.network.zerocopy.zeroCopy;

import com.liujun.network.zerocopy.normal.FileCopy;
import org.junit.Test;

import java.io.File;

/**
 * 进行文件拷贝的比对
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/14
 */
public class FileCopyCompare {

  public static final String SRC = "D:\\java\\test\\randomdata\\bigdata_2G.data";

  @Test
  public void zerocopy() {

    String target = "./zipcopy.data";

    long starttime = System.currentTimeMillis();
    ZeroCopy.INSTANCE.fileCopy(SRC, target);
    long endTime = System.currentTimeMillis();

    System.out.println("零拷贝文件:" + (endTime - starttime));
    new File(target).delete();
  }

  @Test
  public void normalCopy() {

    String target = "./normalcopy.data";

    long normalStartTime = System.currentTimeMillis();
    FileCopy.INSTANCE.fileCopy(SRC, target);
    long normalEndTime = System.currentTimeMillis();

    System.out.println("普通拷贝文件:" + (normalEndTime - normalStartTime));

    new File(target).delete();
  }
}
