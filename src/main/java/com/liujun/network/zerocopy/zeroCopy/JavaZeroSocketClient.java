package com.liujun.network.zerocopy.zeroCopy;

import com.utils.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * 进行客户端的代码
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/12
 */
public class JavaZeroSocketClient {

  private static final String HOST = "127.0.0.1";

  private static final int PORT = 8888;

  public static final JavaZeroSocketClient INSTANCE = new JavaZeroSocketClient();

  public void socketClient(String path) {

    FileInputStream fileInput = null;
    FileChannel readChannel = null;

    SocketChannel socketChannel = null;

    SocketAddress server = new InetSocketAddress(HOST, PORT);

    try {
      fileInput = new FileInputStream(path);
      readChannel = fileInput.getChannel();

      socketChannel = SocketChannel.open(server);
      socketChannel.configureBlocking(true);

      // System.out.println("通道大小:" + readChannel.size());

      long position = 0;
      long sumdata = 0;

      while (sumdata < readChannel.size()) {
        // 将数据写入服务端通道
        sumdata += readChannel.transferTo(sumdata, readChannel.size(), socketChannel);
      }
      // System.out.println("共写入文件大小:" + (sumdata / 1024 / 1024.0) + "M");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.close(socketChannel);
      IOUtils.close(readChannel);
      IOUtils.close(fileInput);
    }
  }
}
