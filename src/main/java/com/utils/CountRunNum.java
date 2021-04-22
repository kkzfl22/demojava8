/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 进宪统计操作
 *
 * @author liujun
 * @since 2021/4/11
 */
public class CountRunNum {

    /**
     * 运行次数
     */
    private AtomicLong dataRunNum = new AtomicLong(0);

    /**
     * 目标匹配的数量
     */
    private AtomicLong targetNum = new AtomicLong(0);

    /**
     * 增量数量
     */
    private static final long INCREMENT = 500000;

    /**
     * 区间开始
     */
    private AtomicLong scopeStart = new AtomicLong(System.currentTimeMillis());

    /**
     * 开始
     */
    private AtomicLong start = new AtomicLong(scopeStart.get());

    /**
     * 使用工厂方法获取实例信息
     *
     * @return 实例
     */
    public static CountRunNum newInstance() {
        return new CountRunNum();
    }

    public void runCount() {

        while (true) {
            long getRunNum = dataRunNum.get();
            long targetRunNum = targetNum.get();

            if (getRunNum != targetRunNum) {
                break;
            }

            boolean casRsp = targetNum.compareAndSet(targetRunNum, targetRunNum + INCREMENT);
            if (casRsp) {
                // 打印信息
                long startTime = start.get();
                long scopeStartValue = scopeStart.get();
                long runNum = dataRunNum.get();

                StringBuilder outData = new StringBuilder();

                long end = System.currentTimeMillis();
                long scopeMulti = end - scopeStartValue;

                outData.append("当前线程:").append(Thread.currentThread().getId()).append(",");
                outData.append("当前共收到").append(runNum).append("次,");
                outData.append("总用时:").append(end - startTime).append("毫秒,");
                outData.append("区间收到").append(INCREMENT).append("次,");
                outData.append("区间用时:").append(scopeMulti).append("毫秒,");

                System.out.println(outData.toString());

                // 设置最新的开始时间
                scopeStart.set(System.currentTimeMillis());
            }
        }

        dataRunNum.incrementAndGet();
    }

    public void print() {
        // 打印信息
        long startTime = start.get();
        long scopeStartValue = scopeStart.get();
        long runNum = dataRunNum.get();

        StringBuilder outData = new StringBuilder();

        long end = System.currentTimeMillis();
        long scopeMulti = end - scopeStartValue;

        outData.append("当前线程:").append(Thread.currentThread().getId()).append(",");
        outData.append("当前共收到").append(runNum).append("次,");
        outData.append("总用时:").append(end - startTime).append("毫秒,");
        outData.append("区间收到").append(INCREMENT).append("次,");
        outData.append("区间用时:").append(scopeMulti).append("毫秒,");

        System.out.println(outData.toString());
    }
}
