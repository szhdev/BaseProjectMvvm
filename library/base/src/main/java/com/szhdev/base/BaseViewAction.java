package com.szhdev.base;

/**
 * Created by szhdev on 2020/12/22.
 */
public enum BaseViewAction {

    LOADING_SHOW,
    LOADING_DISMISS,
    TOAST_SHOW,
    DATA_EMPTY,
    DATA_ERROR,
    NET_ERROR,
    NET_EXCEPTION;


    private String mToastMessage = "";

    public BaseViewAction setToastMessage(String text) {
        if (text == null) return this;
        mToastMessage = text;
        return this;
    }

    public String getToastMessage() {
        return mToastMessage;
    }


    private boolean isNoNet = false;

    public BaseViewAction setIsNoNet(boolean isNoNet) {
        this.isNoNet = isNoNet;
        return this;
    }

    public boolean getIsNoNet() {
        return isNoNet;
    }
}
