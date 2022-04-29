package com.szhdev.base.db;

import java.util.List;

/**
 * Created by szhdev on 2021/2/10.
 */
public class DxKvCommon<T extends CommonJson>  {
    
    DxKv mDxKv;
    private DxKvCommon(){
        mDxKv = DxKvDb.createCommon();
    }

    public void write(String key, T value) {
        mDxKv.write(key,value);
    }
    public T read(String key) {
        return mDxKv.read(key);
    }
    public  T read(String key, T defaultValue) {
        return mDxKv.read(key,defaultValue);
    }


    public void writeAsync(String key, T value,IDxKvCallBack callBack) {

        mDxKv.writeAsync(key,value,callBack);



    }
    public void readAsync(String key,IDxKvCallBack callBack) {

        mDxKv.readAsync(key,callBack);

    }
    public  void  readAsync(String key, T defaultValue,IDxKvCallBack callBack) {
        mDxKv.readAsync(key,defaultValue,callBack);
    }



    public boolean contains(String key) {
        return mDxKv.contains(key);
    }


    public long lastModified(String key) {
        return mDxKv.lastModified(key);
    }


    public void delete(String key) {

        mDxKv.delete(key);
    }


    public void destroy() {

        mDxKv.destroy();
    }


    public List<String> getAllKeys() {
        return mDxKv.getAllKeys();
    }

    private static class holder{
        static DxKvCommon<CommonJson> dxKvCommon = new DxKvCommon<CommonJson>();
    }
    
    public static DxKvCommon<CommonJson> getInstance(){
        return holder.dxKvCommon;
    }
    
    
}
