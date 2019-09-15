package com.liujun.network.zerocopy.normal;

import com.utils.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 普通的文件拷贝
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/14
 */
public class FileCopy {

  public static final FileCopy INSTANCE = new FileCopy();

  public void fileCopy(String src, String target) {
    FileInputStream input = null;
    FileOutputStream output = null;

    try {
      input = new FileInputStream(src);
      output = new FileOutputStream(target);

      byte[] buffer = new byte[1024 * 32];
      int index = -1;

      while ((index = input.read(buffer)) != -1) {
        output.write(buffer, 0, index);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.close(output);
      IOUtils.close(input);
    }
  }
}
