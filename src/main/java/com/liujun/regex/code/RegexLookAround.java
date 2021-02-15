package com.liujun.regex.code;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式中环视
 *
 * @author liujun
 * @version 0.0.1
 */
public class RegexLookAround {

  private static void getRegex(String parrent, String src) {
    Pattern pattern = Pattern.compile(parrent);
    Matcher matcher = pattern.matcher(src);

    while (matcher.find()) {
      System.out.println("match find: true");
      int count = matcher.groupCount();
      System.out.println("count" + count);
      for (int i = 1; i <= count; i++) {
        System.out.println(matcher.group(i));
      }
      //      String dataValue = matcher.replaceAll("bbccdd");
      //      System.out.println(dataValue);
    }

    System.out.println("----------------------");
  }

  private static void regexReplace(String parrent, String src, String replace) {
    Pattern pattern = Pattern.compile(parrent);

    Matcher outdata = pattern.matcher(src);
    System.out.println(outdata);
    while (outdata.find()) {
      String outdata23 = outdata.replaceAll(replace);
      System.out.println(outdata23);
    }
  }

  public static void main(String[] args) {
    getRegex("(?<=\\d+)([A-Z]+)(?=\\d+)", "1111YX2232");
    getRegex(
        "(?<=<li>)([a-z1-9]+)(?=</li>)",
        "<ul><li>item1</li><li>item2</li><li>item3</li><li>item4</li></ul>");
    // regexReplace("(?<=\\d+)([A-Z]+)(?=\\d+)", "1111YX2232", "efg");
    // regexReplace("(?<=\\d+)(\\s?[A-Z]+\\s?)(?=\\d+)", "1111 YX 2232", " efg ");

    // 替换重复的单词
    regexReplace("(\\w+) \\1", "the cat cat is in the the hat", "$1");
  }
}
