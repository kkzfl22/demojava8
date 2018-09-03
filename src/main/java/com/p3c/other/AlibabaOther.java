package com.p3c.other;

/**
 * 1，在使用正则表达式时，利用好其预编译功能，可以有效加快正则匹配速度
 *
 * <p>2，在velocity调用POJO类属性时，建议直接 使用属性名聚会上，模板引擎会自动按规范调用POJO的getXXX， 如果是boolean基本数据类型变量，会自动调用isXXX方法
 *
 * <p>3，后台输送页面的变量必须加$!{{var}--中间是感叹号。说明 ：如果是var==null,或者不存在，那个${var}会直接显示在页面上
 *
 * <p>4，match.ranom()这个方法返回的是double类型，取值的范围0<=x<=1（能够取到0，除0异常）如果想获取整类型的随机数，
 * 不要将x放大10倍，然后取整，直接使用ranom对象的nextint或者nextLong方法.
 *
 * <p>5,获取当前系统的毫秒数用System.currentTimeMillis(),而不是用new Date().getTime();
 *
 * <p>6,不要在视图模板中加入任何复杂的逻辑
 *
 * <p>7，任何数据结构的构造或初始化，都应指定大小，避免因为数据结构无限增长而耗尽内存
 *
 * <p>8，及时清理不在使用的代码段或者配置信息
 */
public class AlibabaOther {}
