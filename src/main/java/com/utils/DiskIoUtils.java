package com.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2019/06/03
 */
public class DiskIoUtils {

  private static final Logger logger = LoggerFactory.getLogger(DiskIoUtils.class);

  public static void close(Closeable diskClose) {
    if (null != diskClose) {
      try {
        diskClose.close();
      } catch (IOException e) {
        e.printStackTrace();
        logger.error("DiskIoUtils close", e);
      }
    }
  }
}
