package com.p3c.name;

/**
 * 各层命名规约。
 *
 * <p>1，Service/DAO层方法名称规约
 *
 * <p>1）获取单个对象的方法用get作为前缀。
 *
 * <p>2）获取多个对象的方法用list用为前缀。
 *
 * <p>3）获取统计值 的方法用count作为前缀。
 *
 * <p>4）插入的方法用save/insert作为前缀
 *
 * <p>5）删除的方法用remove/delete作为前缀
 *
 * <p>6)修改的方法用update作为前缀
 *
 * <p>2,领域模型命名规约:
 *
 * <p>1)数据对象：xxxDO,xxx是数据表名
 *
 * <p>2)数据传输对象:xxxDTO,xxx为业务领域相关的名称
 *
 * <p>3)展示对象:xxxVO,xxx一般为网页名称
 *
 * <p>4)POJO是DO/DTO/BO/VO的统称，禁止命名成xxxPOJO
 */
public class AlibabaLevelName {}
