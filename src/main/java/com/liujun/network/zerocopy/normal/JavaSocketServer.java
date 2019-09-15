package com.liujun.network.zerocopy.normal;

import com.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/12
 */
public class JavaSocketServer {

  public static final int PORT = 8888;

  private static final int BYTE_SIZE = 1024 * 64;

  public static final JavaSocketServer SERVER = new JavaSocketServer();

  private ServerSocket serverSocket = null;

  /** 运行标识 */
  private volatile boolean runflag = true;

  /** 启动服务 */
  public void startServer() {
    try {
      serverSocket = new ServerSocket(PORT);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    System.out.println("服务端已启动，等待客户端连接..");
  }

  /** sokcet接收服务 */
  public void socketReceive() {

    byte[] datacache = new byte[BYTE_SIZE];

    while (runflag) {
      InputStream inputStream = null;
      Socket socket = null;

      try {
        // 侦听并接受到此套接字的连接,返回一个Socket对象
        socket = serverSocket.accept();

        if (socket == null) {
          continue;
        }

        // 根据输入输出流和客户端连接
        // 得到一个输入流，接收客户端传递的信息
        inputStream = socket.getInputStream();

        int readNum = 0;
        int sumNum = 0;

        while ((readNum = inputStream.read(datacache)) != -1) {
          sumNum = sumNum + readNum;
        }
        // System.out.println("共传输:" + (sumNum / 1024 / 1024.0f) + "M");

      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        IOUtils.close(inputStream);
        IOUtils.close(socket);
      }
    }
  }

  public void stopServer() {
    // 将标识符改为不在运行
    this.runflag = false;
    // 关闭服务端
    IOUtils.close(serverSocket);
  }
}
