package com.liujun.network.zerocopy.zeroCopy;

import com.utils.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/12
 */
public class JavaZeroSocketServer {

  private static final int SERVER_PORT = 8888;

  private static final int BYTE_SIZE = 1024 * 4;

  private volatile boolean runFlag = true;

  public static final JavaZeroSocketServer INSTACE = new JavaZeroSocketServer();

  private ServerSocketChannel server;

  public void startServer() {
    try {
      server = ServerSocketChannel.open();
      ServerSocket socket = server.socket();
      socket.bind(new InetSocketAddress(SERVER_PORT));
      socket.setReuseAddress(true);

    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("启动完成");
  }

  public void serverRecivice() {

    ByteBuffer buffer = ByteBuffer.allocateDirect(BYTE_SIZE);

    while (runFlag) {
      SocketChannel socketChannel = null;
      try {
        socketChannel = server.accept();
        socketChannel.configureBlocking(true);

        int readIndex = 0;
        long sumData = 0;

        while ((readIndex = socketChannel.read(buffer)) != -1) {
          sumData = sumData + readIndex;
          // 重置缓冲区
          buffer.rewind();
        }
        // System.out.println("共接收到:" + (sumData / 1024 / 1024f) + "M");
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        IOUtils.close(socketChannel);
      }
    }
  }

  public void stopServer() {
    this.runFlag = false;
    IOUtils.close(server);
  }
}
