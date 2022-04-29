package com.szhdev.base.mvvm;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RelativeLayout;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.szhdev.base.R;
import com.szhdev.base.databinding.ActivityBaseBinding;

import io.reactivex.disposables.Disposable;

/**
 * Created by szhdev on 2021/1/16.
 */
public abstract class DxBaseViewModelActivity<VM extends BaseViewModel, SV extends ViewDataBinding> extends BaseActivity {
    protected VM viewModel;
    protected SV bindingView;
    private ActivityBaseBinding mBaseBinding;
    //数据错误
    private View dataErrorView;
    //数据为空
    private View dateEmptyView;
    //网络错误
    private View netErrorView;

    protected abstract void initView();

    protected abstract void initToolbar();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initViewModel();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        mBaseBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_base, null, false);
        bindingView = DataBindingUtil.inflate(getLayoutInflater(), layoutResID, null, false);
        initToolbar();
        // content
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bindingView.getRoot().setLayoutParams(params);
        RelativeLayout mContainer = (RelativeLayout) mBaseBinding.getRoot().findViewById(R.id.container);
        mContainer.addView(bindingView.getRoot());
        getDelegate().setContentView(mBaseBinding.getRoot());
        bindingView.getRoot().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return false;
            }
        });
        bindingView.getRoot().setOnClickListener(view -> {

        });
        initView();
    }

    protected ViewStub getToolView() {
        return mBaseBinding.tool.getViewStub();
    }

    /**
     * 数据错误
     *
     * @return
     */
    protected int getErrorView() {
        return 0;
    }

    /**
     * 网络错误
     *
     * @return
     */
    protected int getNetErrorView() {
        return 0;
    }

    /**
     * 数据为空
     *
     * @return
     */
    protected int getEmptyView() {
        return 0;
    }

    protected boolean dataErrorClick() {
        return false;
    }

    protected boolean netErrorClick() {
        return false;
    }

    protected boolean dataEmptyClick() {
        return false;
    }

    @Override
    protected void showDataError() {
        if (getErrorView() == 0) {
            return;
        }
        if (dateEmptyView != null && dateEmptyView.getVisibility() != View.GONE) {
            dateEmptyView.setVisibility(View.GONE);
        }
        if (netErrorView != null && netErrorView.getVisibility() != View.GONE) {
            netErrorView.setVisibility(View.GONE);
        }

        if (dataErrorView == null) {
            ViewStub viewStub = mBaseBinding.vsErrorContent.getViewStub();
            viewStub.setLayoutResource(getErrorView());
            dataErrorView = viewStub.inflate();
            // 点击加载失败布局
            dataErrorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataErrorClick()) {
                        dataErrorView.setVisibility(View.GONE);
                        if (bindingView.getRoot().getVisibility() == View.GONE) {
                            bindingView.getRoot().setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            dataErrorView.setVisibility(View.VISIBLE);
        }
        if (bindingView.getRoot().getVisibility() != View.GONE) {
            bindingView.getRoot().setVisibility(View.GONE);
        }
    }

    @Override
    protected void showDataEmpty() {
        if (getEmptyView() == 0) {
            return;
        }
        if (dataErrorView != null && dataErrorView.getVisibility() != View.GONE) {
            dataErrorView.setVisibility(View.GONE);
        }
        if (netErrorView != null && netErrorView.getVisibility() != View.GONE) {
            netErrorView.setVisibility(View.GONE);
        }
        if (dateEmptyView == null) {
            ViewStub viewStub = mBaseBinding.vsEmptyContent.getViewStub();
            viewStub.setLayoutResource(getEmptyView());
            dateEmptyView = viewStub.inflate();
            // 点击加载失败布局
            dateEmptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataEmptyClick()) {
                        dateEmptyView.setVisibility(View.GONE);
                        if (bindingView.getRoot().getVisibility() == View.GONE) {
                            bindingView.getRoot().setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            dateEmptyView.setVisibility(View.VISIBLE);
        }
        if (bindingView.getRoot().getVisibility() != View.GONE) {
            bindingView.getRoot().setVisibility(View.GONE);
        }
    }

    @Override
    protected void showNetError() {
        if (getNetErrorView() == 0) {
            return;
        }
        if (dateEmptyView != null && dateEmptyView.getVisibility() != View.GONE) {
            dateEmptyView.setVisibility(View.GONE);
        }
        if (dataErrorView != null && dataErrorView.getVisibility() != View.GONE) {
            dataErrorView.setVisibility(View.GONE);
        }
        if (netErrorView == null) {
            ViewStub viewStub = mBaseBinding.vsNetError.getViewStub();

            viewStub.setLayoutResource(getNetErrorView());
            netErrorView = viewStub.inflate();
            // 点击加载失败布局
            netErrorView.setOnClickListener(v -> {
                if (netErrorClick()) {
                    netErrorView.setVisibility(View.GONE);
                    if (bindingView.getRoot().getVisibility() == View.GONE) {
                        bindingView.getRoot().setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            netErrorView.setVisibility(View.VISIBLE);
        }
        if (bindingView.getRoot().getVisibility() != View.GONE) {
            bindingView.getRoot().setVisibility(View.GONE);
        }
    }

    @Override
    protected ViewModel initViewModel() {
        Class<VM> viewModelClass = ClassUtil.getViewModel(this);
        if (viewModelClass != null) {
            this.viewModel = new ViewModelProvider(this).get(viewModelClass);
        }
        return this.viewModel;
    }

    /**
     * 禁止改变字体大小
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res != null) {
            Configuration config = res.getConfiguration();
            if (config != null && config.fontScale != 1.0f) {
                config.fontScale = 1.0f;
                res.updateConfiguration(config, res.getDisplayMetrics());
            }
        }
        return res;
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
