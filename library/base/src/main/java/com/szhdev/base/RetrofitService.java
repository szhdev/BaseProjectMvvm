package com.szhdev.base;


import com.szhdev.base.okhttplog.HttpLoggingInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by szhdev on 2021/7/13.
 */
public class RetrofitService {

    private static final long READ_TIMEOUT = 30000;
    private static final long WRITE_TIMEOUT = 30000;
    private static final long CONNECT_TIMEOUT = 30000;

    public <T> T createService(Class<T> service, RetrofitBuilder builder) {
        Retrofit retrofit = builder.build();
        T retT = retrofit.create(service);
        return retT;
    }

    public static RetrofitService create() {
        return new RetrofitService();
    }

    public static class OkHttpClientBuilder {
        private List<Interceptor> mInterceptors;
        private List<Interceptor> mNetInterceptors;
        private long readTimeout = -1;
        private long writeTimeout = -1;
        private long connectTimeout = -1;


        private OkHttpClient.Builder mOkHttpClientBuilder;

        private boolean addDebug = true;
        private HttpLoggingInterceptor.Level mLogLevel = HttpLoggingInterceptor.Level.BODY;

        public OkHttpClientBuilder() {
            mInterceptors = new ArrayList<>();
            mNetInterceptors = new ArrayList<>();
        }

        public static OkHttpClientBuilder create() {
            return new OkHttpClientBuilder();
        }

        public OkHttpClientBuilder okHttpClientBuilder() {

            if (mOkHttpClientBuilder != null) {
                return this;
            }
            mOkHttpClientBuilder = new OkHttpClient.Builder()
                    .readTimeout(readTimeout == -1 ? READ_TIMEOUT : readTimeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeout == -1 ? WRITE_TIMEOUT : writeTimeout, TimeUnit.MILLISECONDS)
                    .connectTimeout(connectTimeout == -1 ? CONNECT_TIMEOUT : connectTimeout, TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(false);

            return this;
        }

        public OkHttpClient.Builder getOkHttpClientBuilder() {
            return mOkHttpClientBuilder;
        }


        public OkHttpClient build() {
            okHttpClientBuilder();
            for (Interceptor interceptor : mInterceptors) {
                mOkHttpClientBuilder.addInterceptor(interceptor);
            }

            for (Interceptor interceptor : mNetInterceptors) {
                mOkHttpClientBuilder.addNetworkInterceptor(interceptor);
            }
            if (addDebug) {
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                httpLoggingInterceptor.setLevel(mLogLevel);
                mOkHttpClientBuilder.addInterceptor(httpLoggingInterceptor);
            }
            return mOkHttpClientBuilder.build();
        }

        public OkHttpClientBuilder setLogLevel(HttpLoggingInterceptor.Level logLevel) {
            mLogLevel = logLevel;
            return this;
        }

        public OkHttpClientBuilder addInterceptor(Interceptor... interceptor) {
            if (interceptor != null) {
                mInterceptors.addAll(Arrays.asList(interceptor));
            }
            return this;
        }

        public OkHttpClientBuilder addNetWorkInterceptor(Interceptor... interceptor) {
            if (interceptor != null) {
                mNetInterceptors.addAll(Arrays.asList(interceptor));
            }
            return this;
        }

        public OkHttpClientBuilder setReadTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }


        public OkHttpClientBuilder setWriteTimeout(long writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }


        public OkHttpClientBuilder setConnectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }


        public OkHttpClientBuilder setAddDebug(boolean addDebug) {
            this.addDebug = addDebug;
            return this;
        }
    }

    public static class RetrofitBuilder {

        private String baseUrl;

        private Converter.Factory coverFactory;
        private CallAdapter.Factory callFactory;

        private OkHttpClient mOkHttpClient;


        public RetrofitBuilder() {

        }

        public static RetrofitBuilder create() {
            return new RetrofitBuilder();
        }

        public Retrofit build() {

            return new Retrofit.Builder()
                    .client(mOkHttpClient)
                    .baseUrl(baseUrl)
                    .addConverterFactory(coverFactory == null ? GsonConverterFactory.create() : coverFactory)
                    .addCallAdapterFactory(callFactory == null ? RxJava2CallAdapterFactory.create() : callFactory)
                    .build();
        }

        public RetrofitBuilder setOkHttpClientBuilder(OkHttpClient client) {
            mOkHttpClient = client;
            return this;
        }

        public RetrofitBuilder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public RetrofitBuilder setCoverFactory(Converter.Factory coverFactory) {
            this.coverFactory = coverFactory;
            return this;
        }

        public RetrofitBuilder setCallFactory(CallAdapter.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

    }

}
