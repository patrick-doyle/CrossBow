package com.crossbow.wear.core;

import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Created by Patrick on 11/07/2015.
 */
public class WearNetworkResponse {

    private static final String RESPONSE_STATUS_CODE = "com.crossbow.wear.response.status";

    private static final String RESPONSE_UUID = "com.crossbow.wear.response.uuid";

    private static final String RESPONSE_DATA = "com.crossbow.wear.response.data";

    private static final String RESPONSE_HEADER_KEYS = "com.crossbow.wear.response.headers_keys";

    private static final String RESPONSE_HEADER_VALUES = "com.crossbow.wear.response.headers_values";

    private static final String RESPONSE_MODIFED = "com.crossbow.wear.response.modified";

    private static final String RESPONSE_NETWORK_TIME = "com.crossbow.wear.response.network_time";

    private static final String RESPONSE_SUCCESS = "com.crossbow.wear.response.success";

    public final String uuid;

    public final boolean success;/** The HTTP status code. */

    public int statusCode;

    /** Raw data from this response. */
    public byte[] data;

    /** Response headers. */
    public Map<String, String> headers;

    /** True if the server returned a 304 (Not Modified). */
    public boolean notModified;

    /** Network roundtrip time in milliseconds. */
    public long networkTimeMs;

    public WearNetworkResponse(boolean success, byte[] data, String uuid, int statusCode, Map<String, String> headers, boolean notModified, long networkTimeMs) {
        this.data = data;
        this.statusCode = statusCode;
        this.headers = headers;
        this.notModified = notModified;
        this.networkTimeMs = networkTimeMs;
        this.uuid = uuid;
        this.success = success;
    }

    public WearNetworkResponse(boolean success, String uuid) {
        this.uuid = uuid;
        this.success = success;
    }

    public WearNetworkResponse(boolean success, String uuid, NetworkResponse response) {
        this(success, uuid);
        if(response != null) {
            data = response.data;
            headers = response.headers;
            notModified = response.notModified;
            networkTimeMs = response.networkTimeMs;
            statusCode = response.statusCode;
        }
    }

    public static WearNetworkResponse fromByteArray(byte[] data) {
        byte[] decompressed = Gzipper.unzip(data);
        DataMap dataMap = DataMap.fromByteArray(decompressed);

        String uuid = dataMap.getString(RESPONSE_UUID);
        boolean success = dataMap.getBoolean(RESPONSE_SUCCESS, false);

        if(success) {
            int statusCode = dataMap.getInt(RESPONSE_STATUS_CODE, 500);
            long networkTime = dataMap.getLong(RESPONSE_NETWORK_TIME, 0);
            boolean modified = dataMap.getBoolean(RESPONSE_MODIFED, false);

            byte[] responseData = dataMap.getByteArray(RESPONSE_DATA);

            String[] headerKeys = TextUtils.split(dataMap.getString(RESPONSE_HEADER_KEYS), ",");
            String[] headerValues = TextUtils.split(dataMap.getString(RESPONSE_HEADER_VALUES), ",");
            Map<String, String> headers = new HashMap<>(headerKeys.length);
            for (int i = 0; i < headerKeys.length; i++) {
                headers.put(headerKeys[i], headerValues[i]);
            }
            return new WearNetworkResponse(true, responseData, uuid, statusCode, headers, modified, networkTime);
        }
        else {
            return new WearNetworkResponse(false, uuid);
        }
    }

    public NetworkResponse getNetworkResponse() {
        return new NetworkResponse(statusCode, data, headers, notModified, networkTimeMs);
    }

    public byte[] toByteArray() {

        DataMap dataMap = new DataMap();
        dataMap.putString(RESPONSE_UUID, uuid);
        dataMap.putBoolean(RESPONSE_SUCCESS, success);

        if(success) {
            dataMap.putInt(RESPONSE_STATUS_CODE, statusCode);
            dataMap.putLong(RESPONSE_NETWORK_TIME, networkTimeMs);
            dataMap.putBoolean(RESPONSE_MODIFED, notModified);

            dataMap.putByteArray(RESPONSE_DATA, data);

            Set<String> headerKeys = headers.keySet();
            String[] keys = new String[headerKeys.size()];
            String[] values = new String[headerKeys.size()];
            int i =0;
            for(String key : headerKeys) {
                keys[i] = key;
                values[i] = headers.get(key);
                i++;
            }

            dataMap.putString(RESPONSE_HEADER_KEYS, TextUtils.join(",", keys));
            dataMap.putString(RESPONSE_HEADER_VALUES, TextUtils.join(",", values));
        }

        return Gzipper.zip(dataMap.toByteArray());
    }
}
