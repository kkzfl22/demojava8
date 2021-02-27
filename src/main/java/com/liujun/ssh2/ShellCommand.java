package com.liujun.ssh2;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 使用java进行shell操作
 *
 * @author liujun
 * @version 0.0.1
 */
public class ShellCommand {

  public static void main(String[] args) {
    JSch jsch = new JSch();
    String host = "192.168.1.248";
    String user = "liujun";
    String passwd = "liujun";
    int port = 22;

    Session session = null;
    try {
      session = jsch.getSession(user, host, port);
      session.setPassword(passwd);
      session.setUserInfo(new MyUserInfo());
      session.connect(30000);

      String outData = runCmd(" cat /proc/cpuinfo| grep \"processor\"| wc -l", session);
      System.out.println("CPU的核数的结果:" + outData);
      String outData2 = runCmd("   cat /proc/meminfo | grep MemTotal", session);
      System.out.println("内存的总大小的结果:" + outData2);
    } catch (JSchException e) {
      e.printStackTrace();
    } finally {
      session.disconnect();
    }
  }

  /**
   * 在远程服务器上执行命令
   *
   * @param cmd 要执行的命令字符串
   * @throws Exception
   */
  public static String runCmd(String cmd, Session session) {

    String data = null;

    InputStream in = null;
    ChannelExec channelExec = null;
    try {
      channelExec = (ChannelExec) session.openChannel("exec");
      channelExec.setCommand(cmd);
      channelExec.connect();
      in = channelExec.getInputStream();

      // 获取数据
      data = getData(in);

    } catch (JSchException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      channelExec.disconnect();
    }

    return data;
  }

  private static String getData(InputStream in) {
    StringBuilder resultData = new StringBuilder();
    try (InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader); ) {
      String buf;
      while ((buf = reader.readLine()) != null) {
        resultData.append(buf);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultData.toString();
  }

  public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
    @Override
    public String getPassword() {
      return null;
    }

    @Override
    public boolean promptYesNo(String message) {
      return true;
    }

    @Override
    public String getPassphrase() {
      return null;
    }

    @Override
    public boolean promptPassphrase(String message) {
      return false;
    }

    @Override
    public boolean promptPassword(String message) {
      return false;
    }

    @Override
    public void showMessage(String message) {
      System.out.println(message);
    }

    @Override
    public String[] promptKeyboardInteractive(
        String destination, String name, String instruction, String[] prompt, boolean[] echo) {
      return null;
    }
  }
}
