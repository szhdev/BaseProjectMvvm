package com.szhdev.base.ntp;

/**
 * Created by szhdev on 2021/11/19.
 */
interface ISntpClient {

    boolean requestTime(String host, int timeoutMilliseconds);

    long getNow();
}
