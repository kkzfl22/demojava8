package com.p3c.server;

/**
 * 服务器
 *
 * <p>1，高并发的服务器建议调小TCP协议的time_wait超时时间，操作系统默认240s，
 * linux修改/etc/sysctl.conf文件中net.ipv4.tcp_fin_timeout=30
 *
 * <p>2,调大服务器所支持的最大文件包柄数，linux默认1024，建议将linux服务器所支持的最大句柄数调高数倍
 *
 * <p>3，给JVM设置-XX:+HeapDumpOnOutOfMemoryError参数，让JVM碰OOM场景时输出dump信息
 *
 * <p>4,线上生产环境，JVM的Xms和Xmx设置一样大小的内存容量，避免在GC后调整堆大小带来的压力
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/05
 */
public class AlibabaServer {}
