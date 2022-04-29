package com.szhdev.base.db;

import java.util.List;

/**
 * Created by szhdev on 2021/2/10.
 */
public interface DxKv {

    <T> void write(String key, T value);
    <T> T read(String key);
    <T> T read(String key, T defaultValue);

    <T> void writeAsync(String key, T value,IDxKvCallBack callBack);
    <T> void readAsync(String key,IDxKvCallBack callBack);
    <T> void readAsync(String key, T defaultValue,IDxKvCallBack callBack);

    boolean contains(String key);
    long lastModified(String key);
    void delete(String key);
    void destroy();
    List<String> getAllKeys();

    void release();

    void forceSync();
}
