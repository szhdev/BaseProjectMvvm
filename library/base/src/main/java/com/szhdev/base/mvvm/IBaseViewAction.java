package com.szhdev.base.mvvm;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.szhdev.base.BaseViewAction;


import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by szhdev on 2020/12/22.
 */
public interface IBaseViewAction {


    void showLoading();

    void dismissLoading();


    MutableLiveData<BaseViewAction> getBaseViewAction();

    void setLifecycleOwner(LifecycleOwner owner);

    LifecycleOwner getLifecycleOwner();


    void setToastMessage(String text);

    CompositeDisposable getCompositeDisposable();

    //处理自定义的异常问题
    void dealCustomeError(CustomeException e);

    //处理网络异常
    void dealNetError(boolean isNoNet);

}
