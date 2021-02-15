// package com.demo.mybatis;
//
// import com.common.entity.PageDataInfo;
// import org.apache.ibatis.executor.parameter.ParameterHandler;
// import org.apache.ibatis.executor.statement.StatementHandler;
// import org.apache.ibatis.mapping.BoundSql;
// import org.apache.ibatis.mapping.MappedStatement;
// import org.apache.ibatis.plugin.Interceptor;
// import org.apache.ibatis.plugin.Intercepts;
// import org.apache.ibatis.plugin.Invocation;
// import org.apache.ibatis.plugin.Plugin;
// import org.apache.ibatis.plugin.Signature;
// import org.apache.ibatis.reflection.MetaObject;
// import org.apache.ibatis.reflection.SystemMetaObject;
//
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.util.Map;
// import java.util.Properties;
//
/// **
// * 利用MyBatis拦截器进行分页 @Intercepts 说明是一个拦截器 @Signature 拦截器的签名 type 拦截的类型 四大对象之一(
// * Executor,ResultSetHandler,ParameterHandler,StatementHandler) method 拦截的方法 args
// * 参数,高版本需要加个Integer.class参数,不然会报错
// */
// @Intercepts({
//  @Signature(
//      type = StatementHandler.class,
//      method = "prepare",
//      args = {Connection.class, Integer.class})
// })
// public class DefinedPageInterceptor implements Interceptor {
//
//  /** 用于标识statement的对象 */
//  private static final String STATEMENT_KEY = "delegate.mappedStatement";
//
//  /** ParameterHandler对象标识 */
//  private static final String PARAMETER_KER = "delegate.parameterHandler";
//
//  /** 查询分页的标识 */
//  private static final String QUERY_PAGE = "Page";
//
//  /** 分页参数标识符 */
//  private static final String PAGE_PARAM = "pageInfo";
//
//  /** 分页的统计SQL */
//  private static final String COUNT_DATA_SQL = "select count(1) from (";
//
//  /** 分页结束符 */
//  private static final String COUNT_END = " )a ";
//
//  /** 分页的关键字 */
//  private static final String LIMIT_KEY = " limit ";
//
//  /** 逗号 */
//  private static final String COMMA = ",";
//
//  /** 用于进行SQL的标识 */
//  private static final String SQL_KEY = "delegate.boundSql.sql";
//
//  /** 默认大小值 */
//  private static final int DEFAULT_SIZE = 1;
//
//  /** 默认页 */
//  private static final int DEFAULT_PAGE = 0;
//
//  @Override
//  public Object intercept(Invocation invocation) throws Throwable {
//    // 获取StatementHandler,默认的是RoutingStatementHandler
//    StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
//    // 获取StatementHandler的包装类
//    MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
//    // 获取查看接口映射的相关信息
//    MappedStatement mappedStatement = (MappedStatement) metaObject.getValue(STATEMENT_KEY);
//    String mapId = mappedStatement.getId();
//    // 拦截以Page结尾的请求，统一实现分页
//    if (mapId.endsWith(QUERY_PAGE)) {
//      // 获取进行数据库操作时管理参数的Handler
//      ParameterHandler parameterHandler = (ParameterHandler) metaObject.getValue(PARAMETER_KER);
//      Map<?, ?> parameter = (Map<?, ?>) parameterHandler.getParameterObject();
//      // 获取请求时的参数
//      PageDataInfo pageDataInfo = (PageDataInfo) parameter.get(PAGE_PARAM);
//      BoundSql boundSql = statementHandler.getBoundSql();
//      // 原始的SQL语句
//      String sql = boundSql.getSql();
//
//      // 查询总条数的SQL语句
//      String countSql = COUNT_DATA_SQL + sql + COUNT_END;
//      Connection connection = (Connection) invocation.getArgs()[0];
//      PreparedStatement countStatement = connection.prepareStatement(countSql);
//      parameterHandler.setParameters(countStatement);
//      ResultSet rs = countStatement.executeQuery();
//
//      if (rs.next()) {
//        pageDataInfo.setTotal(rs.getLong(1));
//      }
//
//      // TODO 当数据统计为0时，应该中断，不再执行查询，但目前查询不查询则报错
//
//      // 每页展示的大小
//      if (pageDataInfo.getPageSize() <= 0) {
//        pageDataInfo.setPageSize(DEFAULT_SIZE);
//      }
//      // 默认页
//      if (pageDataInfo.getPageNum() < 0) {
//        pageDataInfo.setPageNum(DEFAULT_PAGE);
//      }
//
//      // 计算当前开始索引
//      long currStart = pageDataInfo.getPageNum() * pageDataInfo.getPageSize();
//      // 改造后带分页查询的SQL语句
//      String pageSql = sql + LIMIT_KEY + currStart + COMMA + pageDataInfo.getPageSize();
//      metaObject.setValue(SQL_KEY, pageSql);
//    }
//    // 调用原对象方法，进入责任链下一级
//    return invocation.proceed();
//  }
//
//  @Override
//  public Object plugin(Object target) {
//    // 生成Object对象的动态代理对象
//    return Plugin.wrap(target, this);
//  }
//
//  @Override
//  public void setProperties(Properties properties) {
//    // 如果分页每页数量是统一的，可以在这里进行统一配置，也就无需再传入PageInfo信息了
//  }
// }
