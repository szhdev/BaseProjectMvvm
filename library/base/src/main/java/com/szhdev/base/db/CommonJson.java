package com.szhdev.base.db;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by szhdev on 2021/2/10.
 */
public class CommonJson {

    JSONObject value;

    private CommonJson(){

    }

    public static CommonJson create(JSONObject v){
        return new CommonJson().setValue(v);
    }

    public JSONObject getValue() {
        return value==null?new JSONObject():value;
    }

    public CommonJson setValue(JSONObject value) {
        this.value = value;
        return this;
    }
}
