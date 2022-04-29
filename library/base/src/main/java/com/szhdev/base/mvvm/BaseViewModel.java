package com.szhdev.base.mvvm;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.szhdev.base.BaseViewAction;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by szhdev on 2020/12/22.
 */
public class BaseViewModel extends ViewModel implements IBaseViewAction {


    private MutableLiveData<BaseViewAction> mLiveData = null;
    private LifecycleOwner mLifecycleOwner;

    private CompositeDisposable mCompositeDisposable;

    public BaseViewModel() {
        mLiveData = new MutableLiveData<>();
    }


    @Override
    public void showLoading() {
        mLiveData.setValue(BaseViewAction.LOADING_SHOW);
    }

    @Override
    public void dismissLoading() {
        mLiveData.setValue(BaseViewAction.LOADING_DISMISS);
    }

    @Override
    public MutableLiveData<BaseViewAction> getBaseViewAction() {
        return mLiveData;
    }

    @Override
    public void setLifecycleOwner(LifecycleOwner owner) {
        mLifecycleOwner = owner;
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return mLifecycleOwner;
    }

    @Override
    public void setToastMessage(String text) {
        mLiveData.setValue(BaseViewAction.TOAST_SHOW.setToastMessage(text));
    }

    @Override
    public CompositeDisposable getCompositeDisposable() {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        return mCompositeDisposable;
    }

    @Override
    public void dealCustomeError(CustomeException e) {
        if (e.isNetError()) {
            mLiveData.setValue(BaseViewAction.NET_ERROR);
        } else if (e.isDataNull()) {
            mLiveData.setValue(BaseViewAction.DATA_EMPTY);
        } else if (e.isDataError()) {
            mLiveData.setValue(BaseViewAction.DATA_ERROR);
        } else {
            mLiveData.setValue(BaseViewAction.DATA_ERROR);
        }
    }

    @Override
    public void dealNetError(boolean isNoNet) {
        mLiveData.setValue(BaseViewAction.NET_EXCEPTION.setIsNoNet(isNoNet));
    }

}
