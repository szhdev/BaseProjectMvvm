package com.szhdev.base.db.mmkv;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.szhdev.base.db.DxDbContentProvider;
import com.szhdev.base.db.DxKv;
import com.szhdev.base.db.IDxKvCallBack;
import com.szhdev.base.db.paper.DxDBWorkerHandler;
import com.tencent.mmkv.MMKV;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by szhdev on 2021/7/19.
 */
public class DxMMKV implements DxKv {

    private static final String TAG ="DxMMKV";

    private Handler mHandler;
    private boolean mForceSync = false;

    public DxMMKV(){
        if (TextUtils.isEmpty(MMKV.getRootDir())){
            MMKV.initialize(DxDbContentProvider.mContext);
        }
        mHandler =  new Handler(Looper.getMainLooper());
    }
    public DxMMKV(String rootDir){
        mHandler =  new Handler(Looper.getMainLooper());
        if (TextUtils.isEmpty(MMKV.getRootDir())){
            MMKV.initialize(DxDbContentProvider.mContext,rootDir);
        }

    }
    @Override
    public <T> void write(String key, T value) {
        MMKV kv = MMKV.defaultMMKV();
        boolean writeSuccess = false;
        if (value instanceof  String){
            String sv = (String) value;
            writeSuccess =  kv.encode(key,sv);
        }else if (value instanceof  Integer){
            int nv =  ((Integer) value).intValue();
            writeSuccess =   kv.encode(key,nv);
        } else if (value instanceof  Long){
            long nv = ((Long) value).longValue();
            writeSuccess =   kv.encode(key,nv);
        }else if (value instanceof  Float){
            float nv = ((Float) value).floatValue();
            writeSuccess =   kv.encode(key,nv);
        }else if (value instanceof  Boolean){
            boolean nv =  ((Boolean) value).booleanValue();
            writeSuccess =   kv.encode(key,nv);

        }else if (value instanceof  Double){
            double nv = ((Double) value).doubleValue();
            writeSuccess =  kv.encode(key,nv);

        }else if (value instanceof Set){
            Set nv = (Set) value;
            writeSuccess =   kv.encode(key,nv);
        }else {
            try {
                String js = JSON.toJSONString(value);

                 writeSuccess =  kv.encode(key,js);

            }catch (Exception e){
            }

        }
        Log.d(TAG, "write: "+key+" "+writeSuccess);
    }

    @Deprecated
    @Override
    public <T> T read(String key) {
        return null;
    }

    @Override
    public <T> T read(String key, T value) {
        MMKV kv = MMKV.defaultMMKV();


        Object obj = null;
        if (value instanceof  String){
            String sv = (String) value;
            obj= kv.decodeString(key,sv);
        }else if (value instanceof  Integer){
            int nv = ((Integer) value).intValue();
            obj=kv.decodeInt(key,nv);
        } else if (value instanceof  Long){
            long nv = ((Long) value).longValue();
            obj=kv.decodeLong(key,nv);
        }else if (value instanceof  Float){
            float nv = ((Float) value).floatValue();
            obj=kv.decodeFloat(key,nv);
        }else if (value instanceof  Boolean){
            boolean nv =  ((Boolean) value).booleanValue();
            obj=kv.decodeBool(key,nv);

        }else if (value instanceof  Double){
            double nv =  ((Double) value).doubleValue();
            obj=kv.decodeDouble(key,nv);

        }else if (value instanceof Set){
            Set nv = (Set) value;
            obj=kv.decodeStringSet(key,nv);
        }else {

            String sv = kv.decodeString(key,"");
            if (TextUtils.isEmpty(sv))return null;

            try {
                obj=  JSON.parseObject(sv,value.getClass());
            }catch (Exception e){

            }
        }

        if (obj == null)return null;
       return (T) obj;
    }

    @Override
    public <T> void writeAsync(String key, T value, IDxKvCallBack callBack) {
        if(mForceSync){
            write(key,value);
            callBackSuccess(callBack,value);;
            return;
        }
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

    @Deprecated
    @Override
    public <T> void readAsync(String key, IDxKvCallBack callBack) {

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
    public <T> void readAsync(String key, T defaultValue, IDxKvCallBack callBack) {

        if(mForceSync){

            T t = read(key,defaultValue);
            callBackSuccess(callBack,t);

            return;
        }
        getWorkerHandler().post(()->{
            try {
                T t = read(key,defaultValue);
                callBackSuccess(callBack,t);
            }catch (Exception e){
                e.printStackTrace();
                callBackFail(callBack,e);
            }
        });
    }

    @Override
    public boolean contains(String key) {

        MMKV kv = MMKV.defaultMMKV();
        return  kv.contains(key);
    }

    @Override
    public long lastModified(String key) {
        return 0;
    }

    @Override
    public void delete(String key) {
        MMKV kv = MMKV.defaultMMKV();
        kv.remove(key);
    }

    @Override
    public void destroy() {
        MMKV kv = MMKV.defaultMMKV();
        kv.clearAll();
    }

    @Override
    public List<String> getAllKeys() {
        MMKV kv = MMKV.defaultMMKV();
        return Arrays.asList(kv.allKeys());
    }
    private DxDBWorkerHandler getWorkerHandler(){
        return DxDBWorkerHandler.get("dxmmkv");
    }
    @Override
    public void release() {
        DxDBWorkerHandler.destroy("dxmmkv");
    }

    @Override
    public void forceSync() {
        mForceSync = true;
    }
}
