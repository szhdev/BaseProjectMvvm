package com.szhdev.base.mvvm;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @创建者 szhdev
 * @创建时间 2021 2021/1/8/008 10:27
 * @描述 网络请求的基础模型
 */
public abstract class BaseHttpViewModel<BR extends BaseRepo> extends BaseViewModel {
    private CompositeDisposable mCompositeDisposable;
    protected BR mRepo;

    protected void addDisposable(Disposable disposable) {
        if (this.mCompositeDisposable == null) {
            this.mCompositeDisposable = new CompositeDisposable();
        }
        this.mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (this.mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            this.mCompositeDisposable.clear();
        }
    }

    public BaseHttpViewModel() {
        mRepo = initRepo();
    }

    protected abstract BR initRepo();

    @Override
    public void dealCustomeError(CustomeException e) {
        super.dealCustomeError(e);
    }
}
