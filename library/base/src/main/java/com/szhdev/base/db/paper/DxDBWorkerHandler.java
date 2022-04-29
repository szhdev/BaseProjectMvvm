package com.szhdev.base.db.paper;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by szhdev on 2020/9/22.
 */
public class DxDBWorkerHandler {
    private final static ConcurrentHashMap<String, DxDBWorkerHandler> sCache = new ConcurrentHashMap<>(4);

    public static DxDBWorkerHandler get(String name) {
        if (sCache.containsKey(name)) {
            DxDBWorkerHandler cached = sCache.get(name);
            if (cached != null) {
                HandlerThread thread = cached.mThread;
                if (thread.isAlive() && !thread.isInterrupted()) {
                    //LOG.w("get:", "Reusing cached worker handler.", name);
                    return cached;
                }
            }
            //LOG.w("get:", "Thread reference died, removing.", name);
            sCache.remove(name);
        }

       //LOG.i("get:", "Creating new handler.", name);
        DxDBWorkerHandler handler = new DxDBWorkerHandler(name);
        sCache.put(name, handler);
        return handler;
    }

    // Handy util to perform action in a fallback thread.
    // Not to be used for long-running operations since they will
    // block the fallback thread.
    public static void run(Runnable action) {
        get("FallbackCameraThread").post(action);
    }

    private HandlerThread mThread;
    private Handler mHandler;

    private DxDBWorkerHandler(String name) {
        mThread = new HandlerThread(name);
        mThread.setDaemon(true);
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    public Handler get() {
        return mHandler;
    }

    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public Thread getThread() {
        return mThread;
    }
    public static void destroy(String name) {
        if (!sCache.contains(name))return;
        DxDBWorkerHandler handler =sCache.get(name);
        if (handler != null && handler.getThread().isAlive()) {
            handler.getThread().interrupt();
        }
    }
    public static void destroy() {
        for (String key : sCache.keySet()) {
            DxDBWorkerHandler handler =sCache.get(key);
            if (handler != null && handler.getThread().isAlive()) {
                handler.getThread().interrupt();
            }
        }
        sCache.clear();
    }
}
