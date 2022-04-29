package com.szhdev.base.db;

/**
 * Created by szhdev on 2021/5/24.
 */
public interface IDxKvCallBack<T> {
     void onSuccess(T value);
     void onFail(Exception e);
}
