package com.szhdev.base.mvvm;

/**
 * @创建者 szhdev
 * @创建时间 2021 2021/2/19/019 9:54
 * @描述 自定义处理异常报错
 */
public class CustomeException extends Exception {
    public static int DATA_NULL = 0;
    public static int DATA_ERROR = 1;
    public static int NET_ERROR = 2;
    public static int NET_TIME_OUT = 3;
    public static int OTHER = 99;
    private int code;

    public CustomeException(int code, String text, Throwable throwable) {
        super(text, throwable);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public boolean isNetError() {
        return code == NET_ERROR;
    }

    public boolean isNetTimeOut() {
        return code == NET_TIME_OUT;
    }


    public boolean isDataError() {
        return code == DATA_ERROR || code == NET_TIME_OUT;
    }

    public boolean isOther() {
        return code == OTHER;
    }

    public boolean isDataNull() {
        return code == DATA_NULL;
    }

}
