package com.p3c.depend;

/**
 * 二方库依赖
 *
 * <p>定义GAV格式: groupId的格式：com.{公司/BU}.业务线。【子业务线】最多4级
 *
 * <p>artifactId格式:产品线名-模块名 version版本号命名格式:
 *
 * <p>版本号命名方式：主版本号.次版本号。修订号
 *
 * <p>主版本号：产品方向改变或者大规模API不兼容，或者架构不兼容升级
 *
 * <p>次版本号：保持相对兼容，，增加主要功能特性，影响极小的API不兼容
 *
 * <p>修订号：保持完全兼容，修改bug，新增次要功能特性.
 *
 * <p>初始版本号为1.0.0
 *
 *
 * 二方库的新增加或者升级，如果依赖发生变化，则建议使用dependency:resolve前后进行信息比对，如果结果完全不一至，通过depencency:tree命令找出差异点进行<exclude>排除jar
 *
 * 禁止项目中引用相同group和atrfiacted，但version不同的
 *
 *
 * 二方库遵循以下原则
 * 1，精简可控原则， 移除一切不必要的API和依赖，依赖第二方库，尽量是provided引入
 * 2，t稳定可追溯原则。每个版本的变化都应该被记录下来，二库库由谁维护，源码在哪里，都需要能方便地查到.
 *
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/05
 */
public class AlibabaDepend {}
