package com.szhdev.base.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by szhdev on 2021/6/11.
 * NetCheck可以通过head请求导学服务器自己的接口来判断网络是否正常
 */
public class DxNetCheck {


    private static final String TAG = "DxNetCheck";


    private DxNetCheck() {

    }

    public static DxNetCheck create() {
        return new DxNetCheck();
    }

    public INetCheck head(Context context, String url) {
        return new HeadNetCheck(context, url);
    }

    public INetCheck ping(String host) {
        return new PingNetCheck(host);
    }

    public INetCheck pingBaidu() {
        return new PingNetCheck("www.baidu.com");
    }

    public static boolean isWebConnect(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static class PingNetCheck implements INetCheck {

        private String host = "";

        public PingNetCheck(String host) {
            this.host = host;
        }

        @Override
        public boolean start() {
            return sync(3000, 2, 0);
        }

        @Override
        public boolean sync(int connectTimeoutMillisecond, int repeatCount, int delayRetryMillisecond) {
            Log.d(TAG, "ping sync");

            String result = "";
            try {

                String ip = host;// ping 的地址，可以换成任何一种可靠的外网
                String cmd = String.format("ping -c %d -w %d %s", repeatCount, connectTimeoutMillisecond / 1000, ip);
                Log.d(TAG, cmd);
                Process p = Runtime.getRuntime().exec(cmd);// ping网址3次
                StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");

                // kick off stderr
                errorGobbler.start();

                StreamGobbler outGobbler = new StreamGobbler(p.getInputStream(), "STDOUT");
                // kick off stdout
                outGobbler.start();

                // ping的状态
                int status = p.waitFor();
                if (status == 0) {
                    result = "success";
                    return true;
                } else {
                    result = "failed";
                }
            } catch (IOException e) {
                result = "IOException";

            } catch (InterruptedException e) {
                result = "InterruptedException";

            } finally {
                Log.d(TAG, "result = " + result);
            }
            return false;
        }

        @Override
        public void async(int connectTimeoutMillisecond, int repeatCount, int delayRetryMillisecond, NetCallBack callBack) {
            Log.d(TAG, "ping async: ");
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(() -> {
                if (callBack != null) {
                    callBack.netAvailable(sync(connectTimeoutMillisecond, repeatCount, delayRetryMillisecond));
                }
            });
            executor.shutdown();
        }
    }

    public static class HeadNetCheck implements INetCheck {

        final private Context mContext;
        private String mUrl = "";

        public HeadNetCheck(Context context, String url) {
            mContext = context;
            mUrl = url;
        }

        private boolean head(int connectTimeoutMillisecond) {

            int ct = connectTimeoutMillisecond <= 0 ? 3000 : connectTimeoutMillisecond;

            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(ct, TimeUnit.MILLISECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(mUrl).method("HEAD", null).build();
                long start = System.currentTimeMillis();
                okhttp3.Response response = client.newCall(request).execute();
                Log.d(TAG, "head: code : " + response.code() + " cost: " + (System.currentTimeMillis() - start));
                if (response.code() >= 200) {
                    return true;
                }
            } catch (IOException e) {
                Log.d(TAG, "IOException: ");
                e.printStackTrace();
            }
            return false;

        }

        @Override
        public boolean start() {
            return sync(3000, 3, 1000);
        }

        @Override
        public boolean sync(int connectTimeoutMillisecond, int repeatCount, int delayRetryMillisecond) {

            for (int i = 0; i < repeatCount; i++) {

                Log.d(TAG, "sync: repeatCount " + i);
                if (mContext != null && !isWebConnect(mContext)) {
                    continue;
                }
                if (head(connectTimeoutMillisecond)) {
                    return true;
                }
                try {
                    Thread.sleep(delayRetryMillisecond);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return false;
        }

        @Override
        public void async(int connectTimeoutMillisecond, int repeatCount, int delayRetryMillisecond, NetCallBack callBack) {
            Log.d(TAG, "async: ");
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(() -> {
                if (callBack != null) {
                    callBack.netAvailable(sync(connectTimeoutMillisecond, repeatCount, delayRetryMillisecond));
                }
            });
            executor.shutdown();
        }


    }

    public interface NetCallBack {
        void netAvailable(boolean av);
    }

    public interface INetCheck {
        boolean start();

        boolean sync(int connectTimeoutMillisecond, int repeatCount, int delayRetryMillisecond);

        void async(int connectTimeoutMillisecond, int repeatCount, int delayRetryMillisecond, NetCallBack callBack);
    }
}
