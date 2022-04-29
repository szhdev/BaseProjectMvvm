package com.szhdev.base;

/**
 * Created by szhdev on 2020/12/22.
 */
public interface IBaseResp<T> {

    boolean success();

    String errShowMessage();

    T getResData();
}
