package com.liujun.network.zerocopy.normal;

import com.utils.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 进行客户端的代码
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/12
 */
public class JavaSocketClient {

  private static final String HOST = "127.0.0.1";

  private static final int PORT = 8888;

  private static final int BYTE_SIZE = 1024 * 4;

  public static final JavaSocketClient INSTANCE = new JavaSocketClient();

  /**
   * 将指定的文件发送到服务器端
   *
   * @param path 文件路径
   */
  public void socketClient(String path) {

    Socket socket = null;
    OutputStream output = null;
    FileInputStream fileInput = null;
    try {
      fileInput = new FileInputStream(path);
      socket = new Socket(HOST, PORT);

      byte[] cachedata = new byte[BYTE_SIZE];
      int readIndex;

      output = socket.getOutputStream();

      long sumdata = 0;

      while ((readIndex = fileInput.read(cachedata)) != -1) {
        output.write(cachedata, 0, readIndex);
        sumdata = sumdata + readIndex;
      }
      //to do Pipe
      // System.out.println("共写入:" + sumdata / 1024 / 1024.0f + "M");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.close(output);
      IOUtils.close(fileInput);
      IOUtils.close(socket);
    }
  }
}
