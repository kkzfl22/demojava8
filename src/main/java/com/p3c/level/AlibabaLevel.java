package com.p3c.level;

/**
 * 应用分层
 *
 * 1，最外层
 * 1，终端显示层
 * 2，开放接口层
 *
 * 2，WEB层
 *
 * 3，service层，
 *
 * 4，manager层，通用处理，
 * 4.1），对第三方平台封装的层，预处理返回结果及转化异常信息
 * 4.2），对Service层通用能力下沉，如缓存方案，中间件通用处理
 * 4.3），与DAO交互，对多个DAO的组合复用
 *
 * 5，DAO层，与底层的Mysql,Oracle,Hbase等进行数据交互
 *
 * 领域分层模型
 *
 * DO(DATA Object)与数据库库表结构一一对应，通过DAO层向中传输数据源对象
 * DTO(DATA transfer OBJECT)：数据传输对象，server或者manager向外传输的对象
 * BO(Business Object):业务对象，由Server层封装业务逻辑对象
 * AO(Application OBject):应用对象，在web层与Service层之间抽象的对象模型
 * VO(View Object)显示层对象，通常是Web向模板渲染引擎传输的对象
 * Query：数据查询对象，接收上层的查询请求，超过2个参数的查询封装，禁止使用Map类来传输
 *
 *
 *
 *
 *
 *
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/05
 */
public class AlibabaLevel {
}
