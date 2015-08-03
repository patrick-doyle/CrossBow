package com.crossbow.wear.core;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.google.android.gms.wearable.DataMap;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class used to flatten requests into a byte array for transmission over the wear apis
 */
public class RequestSerialUtilOld {

    private static String URL = "com.crossbow.wear.url";

    private static String BODY_CONTENT_TYPE = "com.crossbow.wear.body_content_type";

    private static String POST_BODY = "com.crossbow.wear.post_body";

    private static String TIMEOUT = "com.crossbow.wear.timeout";

    private static String RETRYIES = "com.crossbow.wear.retries";

    private static String PRIORITY = "com.crossbow.wear.priority";

    private static String METHOD = "com.crossbow.wear.method";

    private static String CACHE_KEY = "com.crossbow.wear.cache_key";

    private static String TAG = "com.crossbow.wear.tag";

    private static String UUID = "com.crossbow.wear.uuid";

    private static String TRANSFORMER = "com.crossbow.wear.transformer";

    private static String HEADER_KEYS = "com.crossbow.wear.headers_keys";

    private static String HEADER_VALUES = "com.crossbow.wear.headers_values";

    public static byte[] serializeRequest(String uuid, Request<?> request) throws AuthFailureError, IOException {

        DataMap dataMap = new DataMap();
        dataMap.putByteArray(POST_BODY, request.getBody());
        dataMap.putString(TAG, request.getTag() != null ? request.getTag().toString() : null);
        dataMap.putString(URL, request.getUrl());
        dataMap.putString(BODY_CONTENT_TYPE, request.getBodyContentType());
        dataMap.putString(CACHE_KEY, request.getCacheKey());
        dataMap.putString(UUID, uuid);
        dataMap.putString(PRIORITY, request.getPriority().name());
        dataMap.putInt(TIMEOUT, request.getTimeoutMs());
        dataMap.putInt(METHOD, request.getMethod());
        dataMap.putInt(RETRYIES, request.getRetryPolicy().getCurrentRetryCount());

        Map<String, String> headers = request.getHeaders();
        if(headers != null) {
            int index = 0;
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            String[] headerKeys = new String[entries.size()];
            String[] headerValues = new String[entries.size()];

            for(Map.Entry<String, String> entry : entries) {
                headerKeys[index] = entry.getKey();
                headerValues[index] = entry.getValue();
                index ++;
            }

            dataMap.putStringArray(HEADER_KEYS, headerKeys);
            dataMap.putStringArray(HEADER_VALUES, headerValues);
        }

        if(request instanceof WearRequest) {
            WearRequest wearRequest = WearRequest.class.cast(request);
            dataMap.putString(TRANSFORMER, wearRequest.getTransFormerKey());
        }
        else {
            dataMap.putString(TRANSFORMER, "");
        }

        byte[] data = dataMap.toByteArray();
        //gzip the data
        return Gzipper.zip(data);
    }

    public static WearDataRequest deSerializeRequest(byte[] request) throws IOException {
        //decompress the gzipped data
        byte[] deCompressed = Gzipper.unzip(request);

        DataMap dataMap = DataMap.fromByteArray(deCompressed);

        String url = dataMap.getString(URL);
        String tag = dataMap.getString(TAG);
        String bodyType = dataMap.getString(BODY_CONTENT_TYPE);
        String cacheKey = dataMap.getString(CACHE_KEY);
        String uuid = dataMap.getString(UUID);
        String transformerKey = dataMap.getString(TRANSFORMER);

        int timeout = dataMap.getInt(TIMEOUT, 2500);
        int retryies = dataMap.getInt(RETRYIES, 1);
        int method = dataMap.getInt(METHOD, Request.Method.GET);

        byte[] postBody = dataMap.getByteArray(POST_BODY);
        Request.Priority priority = Request.Priority.valueOf(dataMap.getString(PRIORITY, Request.Priority.NORMAL.name()));

        String[] headerKeys = dataMap.getStringArray(HEADER_KEYS);
        String[] headerValues = dataMap.getStringArray(HEADER_VALUES);

        Map<String, String> headers = new HashMap<>();
        if(headerKeys != null) {
            for (int i = 0; i < headerKeys.length; i++) {
                headers.put(headerKeys[i], headerValues[i]);
            }
        }

        return new WearDataRequest(method, url, uuid, transformerKey, retryies, timeout, cacheKey, tag, bodyType, headers, postBody, priority, new ParamsBundle());
    }
}
