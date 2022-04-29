package com.szhdev.base.ntp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by szhdev on 2021/11/19.
 */
public class NtpManager {


    private static final String TAG = "NtpManager";
    private Context mContext;
    private ISntpClient mSntpClient = null;
    private List<String> mNtpServers = Arrays.asList(
            "cn.pool.ntp.org",
            "cn.ntp.org.cn",
            "ntp1.aliyun.com",
            "ntp2.aliyun.com");
    private int mRetryCount = 3;
    private int mTimeoutMilliseconds = 3000;
    private int mSleepGap = 3000;
    private int mDelayTime = 3000;

    private long mMaybeLastedTime = 0;

    private NtpResultListener mNtpResultListener;

    private static final long DefaultTime = 1501923726923L;//2017-08-05 17:02:06
    private static final long CrashDay = 1637164800000L;//2021-11-18 00:00:00

    private boolean success = false;

    public NtpManager(Context context) {
        mContext = context;
        mSntpClient = new SntpClientV1();
    }

    public NtpManager start() {

        if (success) {
            if (mNtpResultListener != null) mNtpResultListener.onSuccess();
            return this;
        }
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(networkBroadcastReceiver, intentFilter);
        return this;
    }

    public NtpManager start(NtpResultListener listener) {
        mNtpResultListener = listener;
        if (success) {
            if (mNtpResultListener != null) mNtpResultListener.onSuccess();
            return this;
        }
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(networkBroadcastReceiver, intentFilter);
        return this;
    }

    public NtpManager setDelayTime(int time) {
        if (time < 0) return this;
        mDelayTime = time;
        return this;
    }

    public NtpManager setSleepGap(int time) {
        if (time < 0) return this;
        mSleepGap = time;
        return this;
    }

    //可以设置一个认为比较可靠时间作为判断时钟是否正常的判断依据,这样更快
    public NtpManager setMaybeLastedTime(long time) {
        if (time < 0) return this;
        mMaybeLastedTime = time;
        return this;
    }

    public NtpManager addNtpServers(List<String> servers) {
        if (servers == null) return this;
        mNtpServers.addAll(servers);
        return this;
    }

    public NtpManager addNtpServers(int index, List<String> servers) {
        if (servers == null) return this;
        mNtpServers.addAll(index, servers);
        return this;
    }

    public NtpManager setRetryCount(int count) {
        if (count <= 0) return this;
        mRetryCount = count;
        return this;
    }

    public NtpManager setRequestTimeout(int timeoutMilliseconds) {
        if (timeoutMilliseconds <= 0) return this;
        mTimeoutMilliseconds = timeoutMilliseconds;
        return this;
    }

    private BroadcastReceiver networkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                Log.d(TAG, "networkInfo.isAvailable");
                context.unregisterReceiver(networkBroadcastReceiver);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startNTP();
                    }
                }, mDelayTime);

            }
        }
    };

    private void setTime(long time) {
        boolean ok = SystemClock.setCurrentTimeMillis(time);
        Log.d(TAG, "setTime: " + ok);
    }

    //system time manager is set ok
    private boolean fastCheckTimeOk() {
        long current = System.currentTimeMillis();
        if (mMaybeLastedTime == 0) return false;
        boolean ok = current > mMaybeLastedTime;
        Log.d(TAG, " current time: " + current + " system time set is: " + ok);
        return ok;

    }

    private boolean requestOne(String host) {
        for (int i = 0; i < mRetryCount; i++) {
            if (fastCheckTimeOk()) return true;
            if (mSntpClient.requestTime(host, mTimeoutMilliseconds)) {
                setTime(mSntpClient.getNow());
                Log.d(TAG, " host: " + host + " sucess: " + (mSntpClient.getNow()));
                return true;
            } else {
                Log.d(TAG, " host: " + host + "  fail " + 0);
            }
        }
        Log.d(TAG, host + " run: finish");
        return false;
    }

    private void startNTP() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean finish = false;
                while (true) {
                    for (String host : mNtpServers) {
                        if (requestOne(host)) {
                            finish = true;
                            break;
                        }
                    }
                    if (finish) {
                        success = true;
                        if (mNtpResultListener != null) mNtpResultListener.onSuccess();
                        break;
                    }
                    try {
                        Thread.sleep(mSleepGap);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();

    }
}
