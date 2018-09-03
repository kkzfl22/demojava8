package com.p3c.log;

/**
 * 日志规约：
 *
 * <p>1，应用中不可直接使用日志系统(LOG4j、Logback)中的APi，而应依赖于使用日志架构SLF4j中的API,使用门面模式的日志框架，有利于维护和各个类的日志处理方式统一
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; private static final Logger logger =
 * LoggerFactory.getLogger(Abc.class)
 *
 * <p>2,日志文件推荐至少15天，因为有些异常具备以周为频次发生的特点。
 *
 * <p>3,应用中的扩展日志（如打点，临时监控、访问日志等）命名方式appName_logType_logName.log.logType为日志类型，
 * 推荐分类有stats/monitor/visit等；logName为日志描述，这种命名的好昝；通过文件名就可以知道日志文件属于哪个应用，哪种类型，有什么目的，有利于归类查找。
 *
 * <p>4，对trace/debug/info级别的日志输出，必须使用条件形式或者占位符的方式。 正例: if(logger.isdebugEnabled())
 * logger.debug(......); logger.debug("process{},{}",id,symbol);
 *
 * <p>5,避免重复打印日志，否则会浪费磁盘空间，务必在日志文本中设置additivity=false
 *
 * <p>6,异常信息应该包括两类，案发现场信息和异常堆栈信息，如果不处理，那么通过关键字throws往上抛出。
 *
 * <p>7，谨慎的记录日志，生产环境禁止输出debug日志，有选择的输出info日志，如果使用warn记录飓上线时间的业务行为信息，
 * 一定要注意输出量的问题，避免把服务器的磁盘撑爆，并及时删除这些观察日志
 *
 * <p>8,可以使用warn日志级别记录用户输入参数错误的情况，避免当用户投诉时无所适从。
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/03
 */
public class AlibabaLog {}
