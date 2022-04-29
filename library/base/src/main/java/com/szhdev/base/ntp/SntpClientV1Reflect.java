package com.szhdev.base.ntp;

import android.os.SystemClock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by szhdev on 2021/11/19.
 */
public class SntpClientV1Reflect implements ISntpClient {


    static Object mSntp = null;
    static Method mRequestTime = null;
    static Method mGetNtpTime = null;
    static Method mGetNtpTimeReference = null;

    static {

        try {
            Class cla = Class.forName("android.net.SntpClient");
            mSntp = cla.newInstance();
            mRequestTime = cla.getMethod("requestTime", String.class, int.class);
            mGetNtpTime = cla.getMethod("getNtpTime");
            mGetNtpTimeReference = cla.getMethod("getNtpTimeReference");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean requestTime(String host, int timeout) {
        if (mSntp == null || mRequestTime == null) {
            return false;
        }
        try {
            Object obj = mRequestTime.invoke(mSntp, host, timeout);
            return (boolean) obj;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long getNow() {

        if (mGetNtpTime == null || mGetNtpTimeReference == null) return -1;
        try {
            long rt = SystemClock.elapsedRealtime();
            long objNt = (long) mGetNtpTime.invoke(mSntp);
            long objNtr = (long) mGetNtpTimeReference.invoke(mSntp);
            return rt + objNt - objNtr;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
