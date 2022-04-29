package com.szhdev.base.db.paper;

import android.os.Handler;
import android.os.Looper;


import com.szhdev.base.db.DxKv;
import com.szhdev.base.db.IDxKvCallBack;

import java.util.List;

import io.paperdb.Book;
import io.paperdb.Paper;

/**
 * Created by szhdev on 2021/2/10.
 */
public class DxPaper implements DxKv {

    Book mBook;


    private Handler mHandler;
    public DxPaper() {

        mHandler =  new Handler(Looper.getMainLooper());
        mBook =  Paper.book();
    }

    public DxPaper( String dbPath, String dbName) {
        mHandler =  new Handler(Looper.getMainLooper());
        mBook =  Paper.bookOn(dbPath,dbName);
    }

    @Override
    public <T> void write(String key, T value) {
        mBook.write(key,value);
    }

    @Override
    public <T> T read(String key) {
        return mBook.read(key);
    }

    @Override
    public <T> T read(String key, T defaultValue) {
        return mBook.read(key,defaultValue);
    }


    private <T>  void callBackSuccess(IDxKvCallBack callBack, T value){
        if (callBack==null)return;
         mHandler.post(()->{
            callBack.onSuccess(value);
        });
    }

    private <T>  void callBackFail(IDxKvCallBack callBack, Exception e){
        if (callBack==null)return;
        mHandler.post(()->{
            callBack.onFail(e);
        });
    }

    @Override
    public <T> void writeAsync(String key, T value, IDxKvCallBack callBack) {

        getWorkerHandler().post(()->{

            try {
                write(key,value);
                callBackSuccess(callBack,value);
            }catch (Exception e){
                e.printStackTrace();
                callBackFail(callBack,e);
            }
        });
    }

    @Override
    public <T> void readAsync(String key, IDxKvCallBack callBack) {
        getWorkerHandler().post(()->{

            try {
                T rt = read(key);
                callBackSuccess(callBack,rt);
            }catch (Exception e){
                e.printStackTrace();
                callBackFail(callBack,e);
            }
        });
    }

    @Override
    public <T> void readAsync(String key, T defaultValue, IDxKvCallBack callBack) {
        getWorkerHandler().post(()->{

            try {
                T rt = read(key,defaultValue);
                callBackSuccess(callBack,rt);
            }catch (Exception e){
                e.printStackTrace();
                callBackFail(callBack,e);
            }
        });
    }

    @Override
    public boolean contains(String key) {
        return mBook.contains(key);
    }

    @Override
    public long lastModified(String key) {
        return mBook.lastModified(key);
    }

    @Override
    public void delete(String key) {
        mBook.delete(key);
    }

    @Override
    public void destroy() {
        mBook.destroy();
    }
    @Override
    public List<String> getAllKeys() {
        return mBook.getAllKeys();
    }
    private DxDBWorkerHandler getWorkerHandler(){
        return DxDBWorkerHandler.get("dxpaper");
    }
    @Override
    public void release() {
        DxDBWorkerHandler.destroy("dxpaper");
    }

    @Override
    public void forceSync() {

    }
}
