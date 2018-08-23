package com.apache.commons.cli;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.Properties;

/**
 * 使用 Apache Commons CLI 使用基础的
 *
 * <p>Apache Commons CLI 使用 Options 这个类来定义和设置参数，它是所有 Option 实例的容器。在 CLI 中，目前有两种方式来创建
 * Options，一种是通过构造函数，这是最普通也是最为大家所熟知的一种方式；另外一种方法是通过 Options 中定义的工厂方式来实现
 *
 * @author liujun
 * @version 1.0
 * @date 2018-08-23 10:25:00
 */
public class OptionsCodePosixParser {

  public static void main(String[] args) {
    String[] args2 = new String[] {"-h"};
    System.out.println("src value:" + Arrays.toString(args2));
    opertionUse(args2);

    String[] args3 =
        new String[] {
          "-database", "database", "-t", "table", "-files", "file1", "file2", "-Dext=java"
        };
    System.out.println("src value:" + Arrays.toString(args3));
    opertionUse(args3);
  }

  public static void opertionUse(String[] args) {
    // 示例参数如: -type 1 -input path -output path
    // cli定义
    Options opts = new Options();

    opts.addOption("h", "help", false, "print option help ");
    opts.addOption("d", "database", true, "name of database");
    opts.addOption("t", true, "name of table");
    Option fileOptions =
        Option.builder("f").argName("args").longOpt("files").hasArg().desc("file names").build();
    opts.addOption(fileOptions);

    Option javaOptions =
        Option.builder("D")
            .argName("property=name")
            .hasArg()
            .valueSeparator()
            .desc("use value for a property")
            .build();
    opts.addOption(javaOptions);

     //CommandLineParser parser = new PosixParser();
    CommandLineParser parser = new DefaultParser();

    try {
      CommandLine cli = parser.parse(opts, args);

      if (cli.hasOption("h")) {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("Options", opts);
      } else {
        String dataBase = cli.getOptionValue("d");
        System.out.println("database:" + dataBase);
        String table = cli.getOptionValue("t");
        System.out.println("table:" + table);
        String[] files = cli.getOptionValues("t");
        System.out.println("files:" + Arrays.toString(files));
        Properties pro = cli.getOptionProperties("D");
        System.out.println("properties:" + pro.toString());
        String txt = pro.getProperty("ext");
        System.out.println("property value:" + txt);
        System.out.println(Arrays.toString(cli.getOptionValues("D")));
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
