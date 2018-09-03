package com.p3c.exception;

/**
 * 异常处理
 *
 * <p>1，java类库中定义的可以通过预检查方式规避的RuntimeException不应该通过Catch的方式来处理，如IndexOutOfBoundsException,NullpointerException等
 *
 * <p>2，异常不要用来做流程控制，异常设计的初衷是解决程序运行中的各种意外情况，况且异常的处理效率比条件判断方式要低的多。
 *
 * <p>3，catch分清楚稳定代码与非稳定代码。稳定代码指的是无论如何都不会出错的代码。对于非稳定代码的catch，尽可以的在异常类型的区分后，再做对应的异常处理
 * 说明：使用大段的try-catch，将使程序无法根据不同的异常做出正确的应源反应，对不利于定位问题，这是一种不负责任的表现
 *
 * <p>4，捕获异常是为了处理它，不要捕获了却什么都不处理而抛弃之，如果不想处理它，表将该异常抛出给它的调用者。最外层的业务使用者必须处理异常，将其转化为用户可以理解的内容。
 *
 * <p>5，有try-catch放到了事务代码中，catch异常后，如果需要回滚事务，一定要注意rollback事务
 *
 * <p>6，finally块必须对资源对象、流对象进行关闭操作，如果有异常也需要要做try-catch操作
 *
 * <p>7，不能在finally块中使用return，说明：当finally块中的return返回方法结束执行，不会再执行try块中的return语句
 *
 * <p>8,捕获异常与抛异常必须完全匹配，或者捕获异常是抛出异常的父类。
 *
 * <p>9，方法的返回值，可以为null,不加制返回空集或者空对象，必须添加注释充分说明在什么情奖品下会返回null值 ， 调用方需要进行null判断以防止NPE问题
 *
 * <p>10，防止NPE，是程序员的基本修养，注意NPE产生的场景：
 *
 * <p>10.1）当返回类型为基本类型时，return包装类型的对象时，自动拆箱有可能产生NPE.
 *
 * <p>10.2)数据库的查询结果可能为null.
 *
 * <p>10.3)集合里的元素即使isNotEmpty, 取出的数据元素，也可能为null
 *
 * <p>10.4)远程调用返回对象时，一律要求进行空指针判断，以防止NPE
 *
 * <p>10.5)对于Session中获取的数据，建议进行NPE检查，以避免空指针。
 *
 * <p>10.6)级连调用obj.getA().getB().getC()；的一连串调用，易产生NPE.
 *
 * <p>11.定义时区分unchecked/checked异常，避免直接抛出new runtimeException(),更不允许抛出Exception或者Throwable,
 * 应使用业务含义的自定义异常。推荐业界已定义异常，推荐业界已定义的自定义异常，如DAOException/ServiceException等
 *
 * 12,对于翁外的HTTP?API开放接口必须使用"错误码"；应用内部推荐异常抛出；跨应用RPC调用优先考滤RESLT方式，封装isSuccess方法、错误码、“错误简短信息”
 *
 * 13，出现出现重复的代码
 *
 *
 */
public class AlibabaException {}
