package com.demo.function.predicate;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/17
 */
public class PredicateUse {

  enum predEnum {
    NUM_123(
        p -> {
          return p.startsWith("123") ? true : false;
        }),
    NUM_456(
        p -> {
          return "456".equals(p) ? true : false;
        }),
    NUM_789(
        p -> {
          return p.contains("789") ? true : false;
        });
    private Predicate<String> key;

    predEnum(Predicate<String> key) {
      this.key = key;
    }

    public Predicate<String> getKey() {
      return key;
    }

    public void setKey(Predicate<String> key) {
      this.key = key;
    }
  }

  public static void main(String[] args) {

    PredicateUse instance = new PredicateUse();

    boolean runResult =
        predEnum
            .NUM_123
            .getKey()
            .or(predEnum.NUM_456.getKey())
            .and(predEnum.NUM_789.getKey())
            .test("123789");

    System.out.println(runResult);

    int[] vs = new int[] {1, 9, 7, 3};

    Consumer<int[]> ys = Arrays::sort;

    ys.accept(vs);

      Consumer<Object> sysout = System.out::println;

      sysout.accept(vs[1]);

      Function<int[],String> toStrings = Arrays::toString;

      sysout.accept(toStrings.apply(vs));

//   Consumer<Object> sysout = System.out::print;
//      toString.accept(vs);
//      sysout.accept(vs);

  }
}
