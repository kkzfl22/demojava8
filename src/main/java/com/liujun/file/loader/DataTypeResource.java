package com.liujun.file.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/13
 */
public class DataTypeResource {

  /** instance Object */
  public static final DataTypeResource INSTANCE = new DataTypeResource();

  /** resource path */
  private static final String RESOURCE_PATH_NAME = "config/dbtype/";

  /** 数据库的类型文件信息 */
  private static final String[] DATABASE_FILE_NAME =
      new String[] {"sqltype_mysql.properties", "sqltype_oracle.properties"};

  static {
    // load database Column type
    INSTANCE.loadResourceDbColumnType();
  }

  /** load database Column type */
  private void loadResourceDbColumnType() {

    for (String dbTypeFile : DATABASE_FILE_NAME) {
      // 进行文件加载
      loadFile(RESOURCE_PATH_NAME + dbTypeFile);
    }
  }

  private void loadFile(String fileName) {

    // load resource
    Properties properties = new Properties();
    InputStream input = null;

    try {
      input = outFileLoad(fileName);
      System.out.println("当前文件路径:" + fileName);

      // 当外部文件未加载成功，再加载内部文件
      if (input == null) {
        System.out.println("使用内部加载");
        input = this.getClass().getClassLoader().getResourceAsStream(fileName);
      }

      properties.load(input);
      Iterator<Map.Entry<Object, Object>> iter = properties.entrySet().iterator();

      Map.Entry<Object, Object> iten = null;

      while (iter.hasNext()) {
        iten = iter.next();
        String dbTypeKeys = String.valueOf(iten.getKey());
        String dbTypeValue = String.valueOf(iten.getValue());

        System.out.println(dbTypeKeys + ":" + dbTypeValue);
      }

      properties.clear();
    } catch (IOException e) {
      e.printStackTrace();

    } finally {
      if (null != input) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private InputStream outFileLoad(String path) {
    try {
      // 优先执行外部文件加载
      return new FileInputStream(path);
    }
    // 外部文件不存在时，异常忽略，此仅为文件找不到，非错误
    catch (FileNotFoundException e) {
    }

    return null;
  }
}
