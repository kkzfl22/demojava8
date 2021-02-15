package com.liujun.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author liujun
 * @version 0.0.1
 */
public class ResourceClose {

  public void finallyClose(String path) throws IOException {

    FileInputStream input = null;
    BufferedInputStream buffer = null;

    try {
      input = new FileInputStream(path);
      buffer = new BufferedInputStream(input);
      byte[] dataBuffer = new byte[1024];
      while (buffer.read(dataBuffer) != -1) {
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    input.close();
  }

  public void resourceClose(String path) throws IOException {

    System.out.println("start");

    try (FileInputStream input = new FileInputStream(path);
        BufferedInputStream buffer = new BufferedInputStream(input)) {

      byte[] dataBuffer = new byte[1024];
      while (buffer.read(dataBuffer) != -1) {
        System.out.println(new String(dataBuffer));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("finish ");
  }
}
