package com.szhdev.base;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;


import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by szhdev on 2021/1/18.
 */
public abstract class BaseHeaderInterceptor implements Interceptor {

    private static final String TAG = "BaseHeaderInterceptor";

    private Context mContext;
    private boolean mAddSign;

    private Map<String, String> mStringMap;

    private boolean mIsAddRandomKey;
    private static final String KeyRandom = "DxHttpCommonTimeStamp";
    public static final String NAME_PATTERN = "name=\"(.+?)\"";
    private boolean mIsDebug = false;
    private boolean mIsSigToken = true;

    protected abstract String getToken();

    protected abstract void reAddHeader(Map<String, String> addition);

    public BaseHeaderInterceptor(Context context, boolean addSign, Map<String, String> addition, boolean isAddRandomKey) {
        mContext = context;
        mAddSign = addSign;

        mStringMap = addition;
        mIsAddRandomKey = isAddRandomKey;
    }

    public BaseHeaderInterceptor(Context context, boolean isDebug, boolean addSign, Map<String, String> addition, boolean isAddRandomKey) {
        mContext = context;
        mAddSign = addSign;

        mStringMap = addition;
        mIsAddRandomKey = isAddRandomKey;
        mIsDebug = isDebug;
    }

    public BaseHeaderInterceptor(Context context, boolean isDebug, boolean isSigToken, boolean addSign, Map<String, String> addition, boolean isAddRandomKey) {
        mContext = context;
        mAddSign = addSign;

        mStringMap = addition;
        mIsAddRandomKey = isAddRandomKey;
        mIsDebug = isDebug;
        mIsSigToken = isSigToken;
    }

    private String getValue(String reg, String data) {

        Pattern p = Pattern.compile(reg);
        Matcher match = p.matcher(data);
        if (match.find()) {
            return match.group(1);
        }
        return "";
    }

    private Map getNameAndFileName(Headers headers) {

        Map map = new HashMap();
        List<String> listName = headers.values("Content-Disposition");

        for (String data : listName) {
            if (data.indexOf("form-data") >= 0) {
                String reg = "\\sname=\"(.*?)\"";
                String v = getValue(reg, data);
                if (v.length() > 0) {
                    map.put("name", v);
                }
                reg = "\\sfilename=\"(.*?)\"";
                v = getValue(reg, data);
                if (v.length() > 0) {
                    map.put("filename", v);
                }
            }

        }
        return map;
    }

    public Map<String, String> getHttpGetHeader(Map<String, String> params) {
        Map<String, String> headerMap = (mStringMap == null ? new HashMap<>() : new HashMap<>(mStringMap));
        reAddHeader(headerMap);

      /*  headerMap.put("versionName", AppUtil.getApkPackageInfo(mContext).versionName);
        headerMap.put("versionCode", AppUtil.getApkPackageInfo(mContext).versionCode+"");

        headerMap.put("macAddress", "" + AppUtil.tryGetWifiMac(mContext));
        headerMap.put("osVersion", "" + AppUtil.getSystemVersion());*/
        headerMap.put("androidVersionInt", "" + Build.VERSION.SDK_INT);
        headerMap.put("androidVersionStr", "" + Build.VERSION.RELEASE);

        headerMap.put("model", Build.MODEL);//手机的型号;BAH-AL00
        headerMap.put("product", Build.PRODUCT);//整个产品的名称;BAH
        headerMap.put("manufacturer", Build.MANUFACTURER);//获取设备制造商;HUAWEI
        headerMap.put("token", getToken());
        if (mIsDebug) {
            SortedMap<String, String> sortedMap = new TreeMap<String, String>();
            for (Map.Entry<String, String> temp : params.entrySet()) {
                sortedMap.put(temp.getKey(), temp.getValue());
            }
            Log.d(TAG, "getHttpGetHeader: " + sortedMap.toString());
        }
        return headerMap;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {


        Request original = chain.request();
        Map<String, String> map = new HashMap<String, String>();
        HttpUrl httpUrl = original.url();

        String tmstp = System.currentTimeMillis() + "";

        for (int i = 0; i < httpUrl.querySize(); i++) {
            map.put(httpUrl.queryParameterName(i), httpUrl.queryParameterValue(i));
        }
        RequestBody requestBody = original.body();
        if (requestBody != null && original.method().equals("POST")) {


            if (requestBody instanceof FormBody) {
                FormBody oidFormBody = (FormBody) original.body();
                for (int i = 0; i < oidFormBody.size(); i++) {
                    map.put(oidFormBody.name(i), oidFormBody.value(i));
                }

            } else if (requestBody instanceof MultipartBody) {
                MultipartBody body = (MultipartBody) original.body();
                for (MultipartBody.Part part : body.parts()) {

                    try {

                        RequestBody rb = part.body();
                        MediaType mt = rb.contentType();
                        if (mt == null || mt.toString().contains("text") || mt.toString().contains("multipart")) {
                            Map<String, List<String>> mp = part.headers().toMultimap();
                            List<String> list = mp.get("content-disposition");
                            if (list.size() > 0) {

                                String cd = list.get(0);
                                String name = "";
                                Pattern pattern = Pattern.compile(NAME_PATTERN);
                                Matcher matcher = pattern.matcher(cd);
                                if (matcher.find()) {
                                    name = matcher.group(1);
                                }
                                if (!TextUtils.isEmpty(name)) {
                                    String value = "";
                                    if (rb.contentLength() > 0) {
                                        Buffer buffer = new Buffer();
                                        rb.writeTo(buffer);
                                        value = buffer.readUtf8();
                                    }
                                    map.put(name, value);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                MediaType mt = requestBody.contentType();
                if (mt != null) {
                    if (mt.toString().contains("application/json")) {
                        try {

                            Buffer buffer = new Buffer();
                            requestBody.writeTo(buffer);
                            JSONObject jsonObject = new JSONObject(buffer.readUtf8());
                            Iterator<String> iterator = jsonObject.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                map.put(key, "" + jsonObject.get(key));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }


            }
        }
        Request.Builder builder = original.newBuilder();

        if (!map.containsKey(KeyRandom) && mIsAddRandomKey) {
            map.put(KeyRandom, tmstp);
            builder.url(httpUrl.newBuilder().addQueryParameter(KeyRandom, tmstp).build());
        }

        Map<String, String> headerMap = getHttpGetHeader(map);

        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        Request request = builder
                .method(original.method(), original.body())

                .build();
        return chain.proceed(request);
    }
}
