package com.szhdev.base.db;

import android.os.Environment;


import com.szhdev.base.db.mmkv.DxMMKV;
import com.szhdev.base.db.paper.DxPaper;

import java.io.File;

/**
 * Created by szhdev on 2021/2/10.
 */
public class DxKvDb {
    public static DxKv create(){
        return new DxPaper();
    }

    public static DxKv createMMKV(){
        return new DxMMKV();
    }
    public static DxKv createMMKV(String rootDir){
        return new DxMMKV(rootDir);
    }


    public static DxKv create(String dbPath, String dbName){
        return new DxPaper(dbPath,dbName);
    }

    static DxKv createCommon(){
        File file = new File(Environment.getExternalStorageDirectory(),"dxdb");
        return new DxPaper(file.getAbsolutePath(),"DxKv_common");
    }


    public static DxKvCommon<CommonJson> getCommonInstance(){
        return DxKvCommon.getInstance();
    }
}
