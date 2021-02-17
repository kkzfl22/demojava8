package com.demo.parttern.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * java最通用builder构建器
 *
 * @author liujun
 * @version 0.0.1
 */
public class Builder<T> {

  /** 每次调用get()方法时都会调用构造方法创建一个新对象 */
  private final Supplier<T> instance;

  /** 调用的方法信息 */
  private final List<Consumer<T>> methods = new ArrayList<>();

  /**
   * 构建函数的参数
   *
   * @param instance 实例对象
   */
  public Builder(Supplier<T> instance) {
    this.instance = instance;
  }

  /**
   * 创建一个构建器
   *
   * @param instance 创建的对象
   * @param <T> 泛型对象
   * @return 构建器
   */
  public static <T> Builder<T> of(Supplier<T> instance) {
    return new Builder<>(instance);
  }

  /**
   * 添加一个参数的方法
   *
   * @param consumer 添加的方法
   * @param p1 一个参数
   * @param <P1> 参数的泛型
   * @return 构建器对象
   */
  public <P1> Builder<T> with(Consumer1<T, P1> consumer, P1 p1) {
    // 调用一个方法，无返回值的函数
    Consumer<T> data = instance -> consumer.accept(instance, p1);
    methods.add(data);
    return this;
  }

  /**
   * 添加一个两个参数的方法
   *
   * @param consumer 设置的方法
   * @param p1 参数1
   * @param p2 参数2
   * @param <P1> 参数1的泛型
   * @param <P2> 参数2的泛型
   * @return 构建器对象
   */
  public <P1, P2> Builder<T> with(Consumer2<T, P1, P2> consumer, P1 p1, P2 p2) {
    // 调用一个方法，无返回值的函数
    Consumer<T> data = instance -> consumer.accept(instance, p1, p2);
    methods.add(data);
    return this;
  }

  /**
   * 添加三个参数的方法
   *
   * @param consumer 设置的方法
   * @param p1 参数1
   * @param p2 参数2
   * @param p3 参数3
   * @param <P1> 参数1的泛型
   * @param <P2> 参数2的泛型
   * @param <P3> 参数3的泛型
   * @return 构建器对象
   */
  public <P1, P2, P3> Builder<T> with(Consumer3<T, P1, P2, P3> consumer, P1 p1, P2 p2, P3 p3) {
    // 调用方法，无返回值的函数
    Consumer<T> data = instance -> consumer.accept(instance, p1, p2, p3);
    methods.add(data);
    return this;
  }

  /**
   * 创建对象并调用设置方法
   *
   * @return 实例对象
   */
  public T builder() {
    T value = instance.get();
    // 调用相关的设置方法
    methods.forEach(method -> method.accept(value));
    methods.clear();
    return value;
  }

  /**
   * 1个参数
   *
   * @param <T>
   * @param <P1>
   */
  @FunctionalInterface
  public interface Consumer1<T, P1> {
    void accept(T t, P1 p1);
  }

  /**
   * 2个参数
   *
   * @param <T> 调用的方法
   * @param <P1> 方法的参数1
   * @param <P1> 方法的参数2
   */
  @FunctionalInterface
  public interface Consumer2<T, P1, P2> {
    void accept(T t, P1 p1, P2 p2);
  }

  /**
   * 3个参数
   *
   * @param <T> 调用的方法
   * @param <P1> 方法的参数1
   * @param <P1> 方法的参数2
   */
  @FunctionalInterface
  public interface Consumer3<T, P1, P2, P3> {
    void accept(T t, P1 p1, P2 p2, P3 p3);
  }

  /**
   * 大于3个的参数
   *
   * @param <T> 调用的方法
   * @param <List> 参数集合
   */
  @FunctionalInterface
  public interface ConsumerList<T, List> {
    void accept(T t, List data);
  }
}
