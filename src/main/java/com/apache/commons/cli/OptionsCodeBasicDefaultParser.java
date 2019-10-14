package com.apache.commons.cli;

import org.apache.commons.cli.*;

/**
 * 使用 Apache Commons CLI 开发命令行工具
 *
 * <p>Apache Commons CLI 使用 Options 这个类来定义和设置参数，它是所有 Option 实例的容器。在 CLI 中，目前有两种方式来创建
 * Options，一种是通过构造函数，这是最普通也是最为大家所熟知的一种方式；另外一种方法是通过 Options 中定义的工厂方式来实现
 *
 * <p>第一个参数设定这个 option 的单字符名字，
 *
 * <p>第二个参数指明这个 option 是否需要输入数值，
 *
 * <p>第三个参数是对这个 option 的简要描述
 *
 * @author liujun
 * @version 1.0
 * @date 2018-08-23 10:25:00
 */
public class OptionsCodeBasicDefaultParser {

  public static void main(String[] args) {
    String[] args2 = new String[] {"-T 1", "-I d:/input/11", "-O d:/outou/22"};
    OpertionUse(args2);
  }

  public static void OpertionUse(String[] args) {
    // 示例参数如: -type 1 -input path -output path
    // cli定义
    Options opts = new Options();

    Option typeOpt = new Option("T", true, "type msg");
    // 设置必须出现在命令行中
    typeOpt.setRequired(true);
    opts.addOption(typeOpt);

    // 定义-input 输入命令
    Option inputOpt = new Option("I", true, "input file path");
    inputOpt.setRequired(true);
    opts.addOption(inputOpt);

    // 定义-output输出参数
    Option outputOpt = new Option("O", true, "output file path");
    outputOpt.setRequired(true);
    opts.addOption(outputOpt);

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();

    CommandLine cmdList = null;
    try {
      cmdList = parser.parse(opts, args);
    } catch (ParseException e) {
     //
      // 显示帮助信息
      formatter.printHelp("common-cli-mockedandInjectable", opts);

      System.out.println();
      e.printStackTrace();
    }

    System.out.println(cmdList.getOptionValue("T"));
    System.out.println(cmdList.getOptionValue("I"));
    System.out.println(cmdList.getOptionValue("O"));

  }
}
