package com.google.snappy;

/**
 * @author
 * @version 0.0.1
 * @date
 */
public class TestParse {

  public static void main(String[] args) {

    String strindex = "this_(_12)";
    System.out.println(strindex.substring(strindex.indexOf("(") + 1, strindex.length() - 1));

    String precisioninfo = "32,24";

    int precIndex = precisioninfo.indexOf(",");

    String precisionStr = null;
    String scaleStr = null;

    if (precIndex != -1) {
      precisionStr = precisioninfo.substring(0, precIndex);
      scaleStr = precisioninfo.substring(precIndex + 1);
    }

    System.out.println(precisionStr);
    System.out.println(scaleStr);
  }
}
