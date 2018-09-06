package com.p3c.mysql;

/**
 * SQL语句 1，不要使用count(列名)或者count(常量)替代count(*),count(*)是SQL92定义的标准统计行数语法，跟数据无关，跟NULL与非NULL无关。
 * count(*)会统计为NULL的行，而count(列名）不会统计此列为null的值的行
 *
 * <p>2,count(distrinct column)计算该列除NULL外的不重复行数 。注意count(distinct
 * column,column2),如果其中一列全为NULL,要全即使另外一列有不同的值 ，也返回为0
 *
 * <p>3，当某一列的值 全为NULL时，count(column)的返回结果为0，但sum(column)的返回结果为NULLl,因此使用sum()时需注意NPE问题。
 *
 * <p>4，使用ISNULL()来判断 是否为NULL值。 NULL与任何值的直接 比较都为NULL 4.1)NULL<>null返回的结果是NULL，而不false
 * 4.2)NULL=null返回为结果是NULL，而不是true 4.3)NULL<>1 返回的结果是NULL，则不是true
 *
 * <p>5,在代码中写分布逻辑时，若count为0，应直接返回，执行后面的分页语句
 *
 * <p>6，不得使用外键与级别，一切外键概念必须在应用层解决
 *
 * <p>7，禁止使用存储过程，存储过程难以调用和扩展，更没有移植性
 *
 * <p>8，数据订正，要先select,避免出现误删除，确认无误才能执行更新语句
 *
 * <p>9，in操作能避免则避免，若实避免不了，需要仔细评估in后面的集合元素数据，控制在1000以内
 *
 * <p>10，若有全球化需求，所有字符串存储与表示，均以UTF-8编码，注意字符统计函数的区别
 *
 * <p>11,TRUNCATE TABLE比DELETE速度快，且使用的系统和事务日志资源少，但TRUNCATE无事务且不触发trigger，有可能造成事，故不建议在开发代码中使用此语句
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/04
 */
public class AlibabaSQL {}
