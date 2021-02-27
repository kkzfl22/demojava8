package com.utils;

/**
 * 
 * 线程的工具类
 * 
 * @author liujun
 * @version 0.0.1
 */
public class ThreadUtils {

    /**
     * 进行线程的休眠操作
     * 
     * @param numSec
     */
    public static void sleep(int numSec) {
        try {
            Thread.sleep(numSec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
