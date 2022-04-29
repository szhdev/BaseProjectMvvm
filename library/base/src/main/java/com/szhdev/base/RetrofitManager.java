package com.szhdev.base;


import com.szhdev.base.okhttplog.HttpLoggingInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by szhdev on 2020/12/21.
 */
public class RetrofitManager {
    private static long READ_TIMEOUT = 30000;
    private static long WRITE_TIMEOUT = 30000;
    private static long CONNECT_TIMEOUT = 30000;
    private HttpLoggingInterceptor.Level mLogLevel = HttpLoggingInterceptor.Level.BODY;
    private List<Interceptor> mInterceptors = null;
    private List<Interceptor> mNetworkInterceptors = null;
    private OkHttpClient.Builder mOkHttpClientBuilder = null;
    private boolean mIsDebug = true;
    private boolean mIsCache = true;
    private final Map<Class, Object> mServiceMap = new ConcurrentHashMap<>();
    private final Map<Class, Object> mRetrofitMap = new ConcurrentHashMap<>();

    private static final class Holder {
        public static final RetrofitManager instance = new RetrofitManager();
    }

    public static RetrofitManager getInstance() {
        return Holder.instance;
    }

    public RetrofitManager setDebug(boolean debug) {
        mIsDebug = debug;
        return this;
    }

    public RetrofitManager setCache(boolean cache) {
        mIsCache = cache;
        return this;
    }

    public RetrofitManager setLogLevel(HttpLoggingInterceptor.Level level) {
        mLogLevel = level;
        return this;
    }

    public <T> T getService(Class<T> service) {
        return (T) mServiceMap.get(service);
    }

    public OkHttpClient.Builder getOkHttpClientBuilder() {
        return mOkHttpClientBuilder;
    }

    public OkHttpClient.Builder getDefaultOkHttpBuilder() {
        if (mOkHttpClientBuilder != null) {
            return mOkHttpClientBuilder;
        }
        mOkHttpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true);
        return mOkHttpClientBuilder;
    }

    public RetrofitManager setHttpTimeout(long readTimeout, long writeTimeout, long connectTimeout) {
        READ_TIMEOUT = readTimeout;
        WRITE_TIMEOUT = writeTimeout;
        CONNECT_TIMEOUT = connectTimeout;
        return this;
    }

    public RetrofitManager addInterceptor(Interceptor interceptor) {
        if (interceptor == null) {
            return this;
        }
        if (mInterceptors == null) {
            mInterceptors = new ArrayList<>();
        }
        mInterceptors.add(interceptor);
        return this;
    }

    public RetrofitManager addNetworkInterceptor(Interceptor interceptor) {
        if (interceptor == null) {
            return this;
        }
        if (mNetworkInterceptors == null) {
            mNetworkInterceptors = new ArrayList<>();
        }
        mNetworkInterceptors.add(interceptor);
        return this;
    }

    public <T> T createRetrofit(Class<T> service, String url) {
        return this.createRetrofit(service, url, null, null);
    }

    public <T> T createRetrofit(Class<T> service, String url, Converter.Factory cvFactory, CallAdapter.Factory callFactory) {
        return createRetrofit(service, url, cvFactory, callFactory, null);
    }

    public Retrofit getRetrofit(Class service) {
        if (mRetrofitMap.containsKey(service)) {
            return (Retrofit) mRetrofitMap.get(service);
        }
        return null;
    }

    public <T> T createRetrofit(Class<T> service, String url, Converter.Factory cvFactory, CallAdapter.Factory callFactory,
                                OkHttpClient.Builder bd) {
        OkHttpClient.Builder builder = bd;
        if (mIsCache && mServiceMap.containsKey(service)) {
            return (T) mServiceMap.get(service);
        }
        if (builder == null) {
            builder = getDefaultOkHttpBuilder();
        }
        if (mInterceptors != null) {
            for (Interceptor interceptor : mInterceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        if (mNetworkInterceptors != null) {
            for (Interceptor interceptor : mNetworkInterceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
        if (mIsDebug) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(mLogLevel);
            builder.addInterceptor(httpLoggingInterceptor);
        }
        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(cvFactory == null ? GsonConverterFactory.create() : cvFactory)
                .addCallAdapterFactory(callFactory == null ? RxJava2CallAdapterFactory.create() : callFactory)
                .build();
        T retT = retrofit.create(service);
        mRetrofitMap.put(service, retrofit);
        if (mIsCache) {
            mServiceMap.put(service, retT);
        }
        return retT;
    }
}