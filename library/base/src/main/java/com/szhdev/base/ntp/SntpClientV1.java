package com.szhdev.base.ntp;

import android.os.SystemClock;

/**
 * Created by szhdev on 2021/11/19.
 */
public class SntpClientV1 implements ISntpClient {

    SntpClient mSntpClient = null;

    public SntpClientV1() {
        mSntpClient = new SntpClient();
    }

    @Override
    public boolean requestTime(String host, int timeoutMilliseconds) {
        if (mSntpClient == null) return false;
        return mSntpClient.requestTime(host, timeoutMilliseconds);
    }

    @Override
    public long getNow() {
        if (mSntpClient == null) return 0;
        return mSntpClient.getNtpTime() + SystemClock.elapsedRealtime() - mSntpClient.getNtpTimeReference();
    }
}
