package com.szhdev.base.ntp;

import android.content.Context;

import java.util.List;

/**
 * Created by szhdev on 2022/2/9.
 * 单例NtpManager使用方式
 * //1.应用必须要有系统权限
 * //2.可以setDelayTime比较小的值，比如20毫秒，这样一般应用初始化会更快
 * NtpManagerInstance.getInstances(context).setDelayTime(20).start(new NtpResultListener() {
 *
 * @Override public void onSuccess() {
 * <p>
 * }
 * });
 */
public class NtpManagerInstance {

    private static NtpManagerInstance managerInstance;
    private NtpManager manager;

    private NtpManagerInstance(Context context) {
        manager = new NtpManager(context);
    }

    public static NtpManagerInstance getInstances(Context context) {

        if (managerInstance == null) {
            synchronized (NtpManagerInstance.class) {
                if (managerInstance == null) {
                    managerInstance = new NtpManagerInstance(context.getApplicationContext());
                }
            }
        }
        return managerInstance;
    }

    public NtpManagerInstance start(NtpResultListener listener) {
        manager.start(listener);
        return this;
    }

    public NtpManagerInstance setDelayTime(int time) {
        manager.setDelayTime(time);
        return this;
    }

    public NtpManagerInstance setSleepGap(int time) {
        manager.setSleepGap(time);
        return this;
    }

    //可以设置一个认为比较可靠时间作为判断时钟是否正常的判断依据,这样更快
    public NtpManagerInstance setMaybeLastedTime(long time) {
        manager.setMaybeLastedTime(time);
        return this;
    }

    public NtpManagerInstance addNtpServers(List<String> servers) {
        manager.addNtpServers(servers);
        return this;
    }

    public NtpManagerInstance addNtpServers(int index, List<String> servers) {
        manager.addNtpServers(index, servers);
        return this;
    }

    public NtpManagerInstance setRetryCount(int count) {
        manager.setRetryCount(count);
        return this;
    }

    public NtpManagerInstance setRequestTimeout(int timeoutMilliseconds) {
        manager.setRequestTimeout(timeoutMilliseconds);
        return this;
    }

}
