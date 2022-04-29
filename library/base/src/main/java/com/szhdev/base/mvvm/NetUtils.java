package com.szhdev.base.mvvm;

/**
 * @创建者 szhdev
 * @创建时间 2021 2021/6/19/019 15:02
 * @描述 描述信息
 */
public abstract class NetUtils {
    private static NetUtils instance;

    public static void init(NetUtils netUtils) {
        instance = netUtils;
    }

    public static NetUtils getInstance() {
        return instance;
    }

    /**
     * 传递网络判断方法
     *
     * @return
     */
    public abstract boolean isNetConnect();

    /**
     * 传递网络判断错误时错误提示
     * 可不修改使用默认
     *
     * @return
     */
    public String noNetMessage() {
        return "请检查网络链接";
    }
}
