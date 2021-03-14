package com.liujun.asynchronous.nonblocking.common;

/**
 * @author liujun
 * @version 0.0.1
 */
public class ErrorData implements Comparable<ErrorData> {

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String msg;

    public ErrorData(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public int compareTo(ErrorData o) {
        if (this.code > o.getCode()) {
            return -1;
        } else if (this.code < o.getCode()) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ErrorData{");
        sb.append("code=").append(code);
        sb.append(", msg='").append(msg).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
