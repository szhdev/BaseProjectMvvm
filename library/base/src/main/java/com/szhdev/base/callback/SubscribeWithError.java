package com.szhdev.base.callback;

/**
 * Created by szhdev on 2020/12/22.
 */
public interface SubscribeWithError<T> extends SubscribeSuccess<T> {
    void onError(Exception e);
}
