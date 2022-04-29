package com.szhdev.baseprojectmvvm;

import android.app.Dialog;

import com.szhdev.base.mvvm.DxBaseViewModelActivity;

public class MainActivity extends DxBaseViewModelActivity {
    @Override
    protected void initView() {

    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    protected Dialog getLoadingDialog() {
        return null;
    }
}
