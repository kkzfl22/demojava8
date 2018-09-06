package com.p3c.mysql;

/**
 * OORM映射
 *
 * <p>1，在表的查询语句中，不允许使用*，查询哪些列，需要明确写明
 *
 * <p>2，POJO发展类类的布尔值不能加is,而数据库字段必须加is_在resultMap中进行字段与属性的映射
 *
 * <p>3,不使通用的ResultClass作为返回参数，即使所有类与属性都一一对应，即需要定义，返回过来，一个表也必然对应一个属性
 *
 * <p>4，SQL配制参数使用${},需要使用#{},#param#
 *
 * <p>5,ibatis自带的queryForList(String statmentName,int start,int
 * end)不推荐使用，因为这是将查询结果全加到至内存，然后subList进行分页操作
 *
 * 6,不允许拿HashMap和HashTable作为返回查询结果的结果输出
 *
 * 7，更新表记录，必须同时更新表对应的修改时间
 *
 * 8，不要写一个大而全的修改接口，
 * 理由，1容易出错
 * 2，效率低
 * 3，增加binlog存储
 *
 * 9，事务不要滥用，会影响QPS
 *
 * 10<isEqual> 是compareValue是属性值对比常量，一般为数字，表示相等时执行SQL
 * <isNOTEmpty>表示不为空且不为null时执行
 * <isNOTNULL>表示不为NULL值执行
 *
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/05
 */
public class AlibabaORM {}
