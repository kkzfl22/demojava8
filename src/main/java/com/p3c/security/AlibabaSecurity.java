package com.p3c.security;

/**
 *  安全规约
 *
 * 1,隶属于用户个人的页面或者功能必须进行权限控制校验。
 * 防止没有做水平权限校验就可以随意访问、修改、删除别人的数据，比如查看 他人的私信内容、修改他人的订单。
 *
 * 2，用户敏感的数据禁止直接展示，必须对展示数据进行脱敏。
 * 个人手机号码会显示为158****9119,隐藏中间4位，防止 个人隐私泄露
 *
 * 3，用户输入的SQL参数严格参数绑定或者METADATA字段值限定，防止SQL注入，禁止字符串拼接SQL访问数据库
 *
 * 4，用户请求传入的任何参数必须做有效性验证。
 * 忽略参数校验可能导致如下情况
 * 4.1）page size过大导致内存溢出
 * 4.2）恶意的order by导致数据库慢查询
 * 4.3）任意重定向
 * 4.4）SQL注入
 * 4.5）反序列化注入。
 * 4.6）正则输入源串拒绝服务ReDos
 * java代码用正则来验证客户端的输入，有些正则写法验证普通 用户输入没有问题，但是如果攻击人员使用的是特殊构造的字符串来验证，
 *
 * 5，禁止向HTML页面输入未经安全过滤或未正确输入的用户数据。
 *
 * 6，表单、AJAX提交必须执行CSRF安全过滤。
 * CSRF(Cross-site request Forgery)跨站请求伪造是一类常见编程漏洞。对于存在CSRF漏洞的应用或者网站，攻击者可以事先构造好URL，
 * 一旦受害者用户访问，后台便可在用户不知情的情况下对数据库中的用户参数进行相应的修改
 *
 * 7，在使用平台资源时，譬如短信、邮件、电话、下单、支持必须实现正确的重放限制，如数量限制、疲劳度控制、验证码校验，避免被滥刷、资损。
 * 如注册时发送验证码手机，如果没有限制次数和频率，那么可以利用此功能骚扰到其他用户，并造成短信平台资源浪费。
 *
 * 8，针对发贴、评论、发送即时消息等用户生成内容的场景，必须实现防刷、文本内容违禁词过滤等风控策略
 *
 *
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/04
 */
public class AlibabaSecurity {
}
