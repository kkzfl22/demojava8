package com.liujun.pattern;

/**
 * 通过多个if else 来进行代码的扩展
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/07/16
 */
public class TestCaseCode2 {

  public void ifCode(int valueCode) {
    if (valueCode == 1) {
      runMethod(1);
      System.out.println("执行业务方法1");
    }
    if (valueCode == 2) {
      runMethod(2);
      System.out.println("执行业务方法2");
    }
    if (valueCode == 3) {
      System.out.println("执行业务方法3");
    }
    if (valueCode == 4) {
      runMethod(1);
      System.out.println("新增加的业务逻辑4");
    }
    if (valueCode == 5) {
      System.out.println("新增加的业务逻辑5");
    }
  }

  public void runMethod(int code) {
    System.out.println("执行业务逻辑:" + code);
  }

  public void switchCode(int code) {
    switch (code) {
      case 1:
        runSwitchMethod(1);
        System.out.println("执行业务方法1");
        break;
      case 2:
        runSwitchMethod(2);
        System.out.println("执行业务方法2");
        break;
      case 3:
        System.out.println("执行业务逻辑3");
        break;
      case 4:
        runSwitchMethod(1);
        System.out.println("新增加的业务逻辑4");
        break;
      case 5:
        System.out.println("新增加的业务逻辑5");
        break;
      default:
        System.out.println("执行默认操作");
        break;
    }
  }

  public void runSwitchMethod(int code) {
    System.out.println("执行业务逻辑:" + code);
  }

  public void ifelseCode(int code) {
    if (code == 1) {
      runCode(1);
      System.out.println("执行业务方法1");
    } else if (code == 2) {
      runCode(2);
      System.out.println("执行业务方法2");
    } else if (code == 3) {
      System.out.println("执行业务方法3");
    } else if (code == 4) {
      runCode(1);
      System.out.println("新增加业务逻辑4");
    }
    else if (code == 5) {
      System.out.println("新增加业务逻辑5");
    }
  }

  public void runCode(int code) {
    System.out.println("执行业务逻辑:" + code);
  }
}
