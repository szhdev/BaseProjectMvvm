package com.szhdev.base.db.kyro;

import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.szhdev.base.db.CommonJson;
import com.szhdev.base.db.util.AESUtils;


/**
 * Created by szhdev on 2021/2/10.
 */
public class CommonJsonSerializer extends Serializer<CommonJson> implements Encode {

    @Override
    public void write(Kryo kryo, Output output, CommonJson object) {
        String v = AESUtils.encrypt(key,object.getValue().toString());

        kryo.writeObject(output,v);
    }

    @Override
    public CommonJson read(Kryo kryo, Input input, Class<CommonJson> type) {
        String v = kryo.readObject(input,String.class);

        v = AESUtils.decrypt(key,v);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(v);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonJson.create(jsonObject);


    }
}
