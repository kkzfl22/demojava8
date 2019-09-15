package com.liujun.network.zerocopy.zeroCopy;

import com.utils.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 进行文件的零拷贝操作
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/14
 */
public class ZeroCopy {

  public static final ZeroCopy INSTANCE = new ZeroCopy();

  public void fileCopy(String src, String outFile) {
    FileInputStream input = null;
    FileChannel inputchannel = null;
    FileOutputStream outputStream = null;
    FileChannel outputChannel = null;

    try {
      input = new FileInputStream(src);
      inputchannel = input.getChannel();
      outputStream = new FileOutputStream(outFile);
      outputChannel = outputStream.getChannel();

      outputChannel.transferFrom(inputchannel, 0, inputchannel.size());

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.close(outputChannel);
      IOUtils.close(outputStream);
      IOUtils.close(inputchannel);
      IOUtils.close(input);
    }
  }
}
