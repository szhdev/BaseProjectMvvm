package com.szhdev.base.mvvm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.reactivex.disposables.Disposable;

/**
 * Created by szhdev on 2021/1/16.
 */
public abstract class DxBaseViewModelFragment<VM extends BaseViewModel, SV extends ViewDataBinding> extends BaseFragment {
    protected VM viewModel;
    protected SV bindingView;
    protected Activity activity;

    protected abstract void initView();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initViewModel();
        bindingView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), getLayoutId(), null, false);
        initView();
        return bindingView.getRoot();
    }


    protected abstract int getLayoutId();

    @Override
    protected ViewModel initViewModel() {
        Class<VM> viewModelClass = ClassUtil.getViewModel(this);
        if (viewModelClass != null) {
            this.viewModel = new ViewModelProvider(this).get(viewModelClass);
        }
        return this.viewModel;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null && viewModel.getCompositeDisposable() != null) {
            viewModel.getCompositeDisposable().clear();
        }

    }

    public void addSubscription(Disposable s) {
        viewModel.getCompositeDisposable().add(s);
    }
}
