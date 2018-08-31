package com.p3c.concurrent.threadpoolexecutor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/** 推荐使用的DataForm对象信息，可保证线程安全 */
public class DataFormatUtils {

  public static final DataFormatUtils INSTANCE = new DataFormatUtils();

  private static final ThreadLocal<DateFormat> df =
      new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
          return new SimpleDateFormat("yyyy-MM-dd");
        }
      };

  public static DateFormat getFormat() {
    return df.get();
  }

  public static void main(String[] args) {
    Thread thr1 =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                DateFormat dfitem = DataFormatUtils.getFormat();
                System.out.println(dfitem);
              }
            });

    Thread thr2 =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                DateFormat dfitem = DataFormatUtils.getFormat();
                System.out.println(dfitem);
              }
            });

    thr1.start();
    thr2.start();
  }
}
