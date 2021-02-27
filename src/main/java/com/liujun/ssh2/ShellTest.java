package com.liujun.ssh2;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import java.io.FilterInputStream;
import java.io.IOException;

/**
 * 使用java进行shell操作
 *
 * @author liujun
 * @version 0.0.1
 */
public class ShellTest {

  public static void main(String[] args) throws JSchException {
    JSch jsch = new JSch();
    String host = "192.168.1.248";
    int port = 22;
    String user = "liujun";
    Session session = jsch.getSession(user, host, 22);
    String passwd = "liujun";
    session.setPassword(passwd);
    session.setUserInfo(new MyUserInfo());
    session.connect(30000);
    Channel channel = session.openChannel("shell");
    // ((ChannelShell)channel).setAgentForwarding(true);
    // 使用Window的问题
    channel.setInputStream(
        new FilterInputStream(System.in) {
          @Override
          public int read(byte[] b, int off, int len) throws IOException {
            return in.read(b, off, (len > 1024 ? 1024 : len));
          }
        });

    channel.setOutputStream(System.out);
    // 去除控制台彩色输出
    ((ChannelShell) channel).setPtyType("vt102");
    // ((ChannelShell) channel).setEnv("LANG", "zh_CN");
    channel.connect(3 * 1000);
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
      System.out.println("输出:" + message);
    }

    @Override
    public String[] promptKeyboardInteractive(
        String destination, String name, String instruction, String[] prompt, boolean[] echo) {
      return null;
    }
  }
}
