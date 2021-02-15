package com.liujun.io;

import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author liujun
 * @version 0.0.1
 */
public class TestResourceClose {

  private static final String OUT_FILE = "testFile.file";

  @Test
  public void finallyClose() throws IOException {
    outWrite();

    ResourceClose instance = new ResourceClose();
    instance.finallyClose(OUT_FILE);
  }

  @Test
  public void resourceClose() throws IOException {
    outWrite();

    ResourceClose instance = new ResourceClose();
    instance.resourceClose(OUT_FILE);
  }

  private void outWrite() {
    FileOutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(OUT_FILE);
      outputStream.write("wreta data".getBytes());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (null != outputStream) {
        try {
          outputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @After
  public void clean() {
    File outfile = new File(OUT_FILE);
    outfile.delete();
  }
}
