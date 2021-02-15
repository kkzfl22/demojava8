package com.liujun.base.zfl;

/**
 * 动物类信息
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/27
 */
public class Animal {

  public void eat() {
    System.out.println("动物开始吃饭");
  }

  public void run() {
    System.out.println("动物开始奔跑");
  }

  public static void main(String[] args) {
    Animal ani1 = new Animal();
    ani1.eat();
    ani1.run();

    Dog dog1 = new Dog();
    dog1.eat();
    dog1.run();
    dog1.jump();

    Animal anmiItem = new Dog();
    anmiItem.run();
    anmiItem.eat();
  }
}

class Dog extends Animal {

  public void eat() {
    System.out.println("狗狗开始吃饭");
  }

  public void run() {
    System.out.println("狗狗开始奔跑");
  }

  public void jump() {
    System.out.println("开始开始乱跳");
  }
}
