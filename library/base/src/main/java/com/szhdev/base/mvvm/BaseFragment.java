package com.szhdev.base.mvvm;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.szhdev.base.BaseViewAction;


/**
 * Created by szhdev on 2020/12/22.
 */
public abstract class BaseFragment extends LazyLoadBaseFragment {

    private Dialog loadingDialog;

    private IBaseViewAction mIBaseViewAction;

    protected abstract ViewModel initViewModel();

    protected abstract Dialog getLoadingDialog();


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IBaseViewAction viewModel = (IBaseViewAction) initViewModel();
        mIBaseViewAction = viewModel;
        viewModel.getBaseViewAction().observe(this, baseViewAction -> {
            if (baseViewAction == BaseViewAction.LOADING_SHOW) {
                showLoading();
            } else if (baseViewAction == BaseViewAction.LOADING_DISMISS) {
                dismissLoading();
            } else if (baseViewAction == BaseViewAction.TOAST_SHOW) {
                toast(baseViewAction.getToastMessage());
            } else if (baseViewAction == BaseViewAction.DATA_EMPTY) {
                showDataEmpty();
            } else if (baseViewAction == BaseViewAction.DATA_ERROR) {
                showDataError();
            } else if (baseViewAction == BaseViewAction.NET_ERROR) {
                showNetError();
            } else if (baseViewAction == BaseViewAction.NET_EXCEPTION) {
                dealNetException(baseViewAction.getIsNoNet());
            }
        });
        viewModel.setLifecycleOwner(this);
    }

    protected void dealNetException(boolean isNoNet) {

    }

    protected void showDataError() {
    }

    protected void showDataEmpty() {
    }

    protected void showNetError() {
    }

    protected void toast(String text) {
        if (!TextUtils.isEmpty(text)) {
            Toast.makeText(this.getActivity().getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }
    }

    protected void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    protected void showLoading() {
        if (loadingDialog == null) {
            Dialog pd = getLoadingDialog();
            if (pd != null) {
                loadingDialog = pd;
            } else {
                loadingDialog = new ProgressDialog(getActivity());
                loadingDialog.setCancelable(false);
                loadingDialog.setCanceledOnTouchOutside(false);
            }
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }

    }


}
