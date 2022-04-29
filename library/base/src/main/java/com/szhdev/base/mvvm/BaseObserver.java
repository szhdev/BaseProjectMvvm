package com.szhdev.base.mvvm;

import com.google.gson.JsonParseException;
import com.szhdev.base.IBaseResp;
import com.szhdev.base.callback.SubscribeSuccess;
import com.szhdev.base.callback.SubscribeWithError;

import org.json.JSONException;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by szhdev on 2020/12/22.
 */
public class BaseObserver<T> implements Observer<T> {


    private boolean mShowProgress = true;
    private boolean mShowToast = true;

    private IBaseViewAction mIBaseViewAction;

    private SubscribeSuccess<T> mConsumer;

    private boolean mIsDataMustNotNull = true;

    public BaseObserver<T> setDataMustNotNull(boolean dataMustNotNull) {
        mIsDataMustNotNull = dataMustNotNull;
        return this;
    }


    public BaseObserver<T> setObserverSuccess(SubscribeSuccess<T> consumer) {
        mConsumer = consumer;
        return this;
    }

    public static BaseObserver create() {
        return new BaseObserver();
    }

    private BaseObserver() {

    }

    BaseObserver(IBaseViewAction baseViewAction) {
        mIBaseViewAction = baseViewAction;
    }

    BaseObserver setViewAction(IBaseViewAction baseViewAction) {
        mIBaseViewAction = baseViewAction;
        return this;
    }


    public BaseObserver setShow(boolean showProgress, boolean showToast) {
        mShowProgress = showProgress;
        mShowToast = showToast;

        return this;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        if (NetUtils.getInstance() != null && !NetUtils.getInstance().isNetConnect()) {
            if (!d.isDisposed()) {
                d.dispose();
            }
            dealError(new CustomeException(CustomeException.NET_ERROR, NetUtils.getInstance().noNetMessage(), null));
            return;
        }
        if (mIBaseViewAction != null && mIBaseViewAction.getCompositeDisposable() != null) {
            mIBaseViewAction.getCompositeDisposable().add(d);
        }
        if (mShowProgress && mIBaseViewAction != null) {
            mIBaseViewAction.showLoading();
        }
    }

    @Override
    public void onNext(@NonNull T o) {

        if (mConsumer != null) {
            if (o == null) {
                dealError(new CustomeException(CustomeException.DATA_ERROR, Const.ApiMsg + "数据空", null));
                return;
            }
            if (o instanceof IBaseResp) {
                IBaseResp br = (IBaseResp) o;
                if (br.success()) {
                    if (br.getResData() == null && mIsDataMustNotNull) {
                        dealError(new CustomeException(CustomeException.DATA_NULL, Const.ApiMsg + "空数据对象", null));
                    } else {
                        mConsumer.onSuccess(o);
                    }
                } else {
                    dealError(new CustomeException(CustomeException.DATA_ERROR, Const.ApiMsg + br.errShowMessage(), null));
                }
            } else {
                mConsumer.onSuccess(o);
            }
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (mShowProgress && mIBaseViewAction != null) {
            mIBaseViewAction.dismissLoading();
        }
        String text = "";
        int error_code;
//        if (e instanceof HttpException) {
//            HttpException httpException = (HttpException) e;
//            text = "网络错误";
//            error_code = CustomeException.NET_ERROR;
//        }else
        if (e instanceof SocketTimeoutException) {
            text = "网络中断，已超时";
            error_code = CustomeException.NET_TIME_OUT;
        } else if (e instanceof SocketException) {
            text = "网络中断，请检查您的网络状态";
            error_code = CustomeException.NET_ERROR;
        } else if (e instanceof UnknownHostException) {
            text = "请检查网络链接";
            error_code = CustomeException.NET_ERROR;
        } else if (e instanceof JsonParseException || e instanceof JSONException/*|| e instanceof ParseException*/) {
            text = "解析错误";
            error_code = CustomeException.OTHER;
        } else if (e instanceof SSLHandshakeException) {
            //javax.net.ssl.SSLHandshakeException: Unacceptable certificate
            text = "请更新正确的系统时间";
            error_code = CustomeException.NET_ERROR;
        } else if (e instanceof SSLPeerUnverifiedException) {
            //javax.net.ssl.SSLPeerUnverifiedException: Hostname
            text = "当前网络需要认证登录";
            error_code = CustomeException.NET_ERROR;
        } else {
            text = Const.ApiMsg + "未知错误";
            error_code = CustomeException.OTHER;
        }
        CustomeException netException = new CustomeException(error_code, text, e);
        dealError(netException);
    }

    private void dealError(CustomeException e) {
        if (mIBaseViewAction != null) {
            if (mShowToast) {
                mIBaseViewAction.setToastMessage(e.getMessage());
            }
            mIBaseViewAction.dealCustomeError(e);
            if (e.isNetError() || e.isNetTimeOut()) {
                mIBaseViewAction.dealNetError(e.isNetError());
            }
        }
        if (mConsumer != null && mConsumer instanceof SubscribeWithError) {
            SubscribeWithError err = (SubscribeWithError) mConsumer;
            err.onError(e);
        }
    }

    @Override
    public void onComplete() {
        if (mShowProgress && mIBaseViewAction != null) {
            mIBaseViewAction.dismissLoading();
        }
    }
}
