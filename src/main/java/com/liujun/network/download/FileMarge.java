package com.liujun.network.download;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2018/10/19
 */
public class FileMarge {

  public static final FileMarge MARGEINSTANCE = new FileMarge();

  public static void main(String[] args) {

    String outPath = "D:/java/test/meda/sksj/084/";
    String margeName = "084.ts";

    MARGEINSTANCE.margetFile(outPath, outPath + margeName);

    System.out.println("marger over :" + outPath);
  }

  public void margetFile(String inputPath, String margeOutFile) {
    File readfile = new File(inputPath);

    if (readfile.exists()) {
      String[] name = readfile.list();

      Arrays.sort(name);

      for (int i = 0; i < name.length; i++) {
        marge(inputPath + "/" + name[i], margeOutFile);
      }
    }
  }

  public void marge(String input, String output) {
    FileInputStream inputStream = null;
    FileOutputStream outputStream = null;
    try {
      inputStream = new FileInputStream(input);
      outputStream = new FileOutputStream(output, true);

      byte[] buffer = new byte[8 * 1024];
      int readIndex = -1;

      while ((readIndex = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, readIndex);
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e2) {
      e2.printStackTrace();
    } finally {
      IOUtils.closeQuietly(outputStream);
      IOUtils.closeQuietly(inputStream);
    }

    new File(input).delete();
  }
}
