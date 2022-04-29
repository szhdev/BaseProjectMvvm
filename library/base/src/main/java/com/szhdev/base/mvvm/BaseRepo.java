package com.szhdev.base.mvvm;


import com.szhdev.base.SchedulersUtils;
import com.szhdev.base.callback.SubscribeSuccess;

import io.reactivex.Observable;


/**
 * Created by szhdev on 2020/12/22.
 */
public class BaseRepo {


    IBaseViewAction mIBaseViewAction = null;

    public BaseRepo() {

    }

    public BaseRepo(IBaseViewAction iBaseViewAction) {
        mIBaseViewAction = iBaseViewAction;
    }

    protected void setIBaseViewAction(IBaseViewAction iBaseViewAction) {
        mIBaseViewAction = iBaseViewAction;
    }

    public <T> void io2main(Observable<T> observable, BaseObserver baseObserver) {
        if (baseObserver == null) {
            baseObserver = BaseObserver.create();
        }

        baseObserver.setViewAction(mIBaseViewAction);
        SchedulersUtils.io2main(observable).subscribe(
                baseObserver
        );

    }

    public <T> void io2main(Observable<T> observable, SubscribeSuccess<T> observerSuccess) {

        io2main(observable, observerSuccess, false);

    }

    public <T> void io2main(Observable<T> observable, SubscribeSuccess<T> observerSuccess, boolean isDataMustNotNull) {
        io2main(observable, observerSuccess, isDataMustNotNull, true, true);
    }


    public <T> void io2main(Observable<T> observable, SubscribeSuccess<T> observerSuccess, boolean isDataMustNotNull, boolean showProgress, boolean showToast) {
        SchedulersUtils.io2main(observable).subscribe(
                new BaseObserver<T>(mIBaseViewAction)
                        .setObserverSuccess(observerSuccess)
                        .setShow(showProgress, showToast)
                        .setDataMustNotNull(isDataMustNotNull)
        );
    }

    public <T> void io2io(Observable<T> observable, SubscribeSuccess<T> observerSuccess) {

        io2io(observable, observerSuccess, false);

    }

    public <T> void io2io(Observable<T> observable, BaseObserver baseObserver) {
        if (baseObserver == null) {
            baseObserver = BaseObserver.create();
        }

        baseObserver.setViewAction(mIBaseViewAction);

        SchedulersUtils.io2io(observable).subscribe(
                baseObserver
        );
    }

    public <T> void io2io(Observable<T> observable, SubscribeSuccess<T> observerSuccess, boolean isDataMustNotNull, boolean showProgress, boolean showToast) {

        SchedulersUtils.io2io(observable).subscribe(
                new BaseObserver<T>(mIBaseViewAction)
                        .setObserverSuccess(observerSuccess)
                        .setShow(showProgress, showToast)
                        .setDataMustNotNull(isDataMustNotNull)
        );
    }

    public <T> void io2io(Observable<T> observable, SubscribeSuccess<T> observerSuccess, boolean isDataMustNotNull) {

        io2io(observable, observerSuccess, isDataMustNotNull, true, true);
    }
}
