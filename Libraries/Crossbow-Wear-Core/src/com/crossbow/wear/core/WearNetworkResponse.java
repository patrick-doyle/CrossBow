package com.crossbow.wear.core;

import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.google.android.gms.wearable.DataMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Internal class that wraps the volley NetworkResponse and can serialized
 */
public class WearNetworkResponse {

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

    public static WearNetworkResponse fromByteArray(byte[] data) throws IOException {
        byte[] decompressed = Gzipper.unzip(data);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decompressed);
        DataInputStream dataStream = new DataInputStream(byteArrayInputStream);
        String uuid = SerialUtil.readString(dataStream);
        boolean success = dataStream.readBoolean();
        int statusCode = dataStream.readInt();
        long networkTime = dataStream.readLong();
        boolean modified = dataStream.readBoolean();
        byte[] responseData = SerialUtil.readBytes(dataStream);
        Map<String, String> headers = SerialUtil.readMap(dataStream);
        return new WearNetworkResponse(success, responseData, uuid, statusCode, headers, modified, networkTime);
    }

    public NetworkResponse getNetworkResponse() {
        return new NetworkResponse(statusCode, data, headers, notModified, networkTimeMs);
    }

    public byte[] toByteArray(){

        int size = data != null ? data.length : 32;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        try {
            SerialUtil.writeString(dataOutputStream, uuid);
            dataOutputStream.writeBoolean(success);
            dataOutputStream.writeInt(statusCode);
            dataOutputStream.writeLong(networkTimeMs);
            dataOutputStream.writeBoolean(notModified);
            SerialUtil.writeBytes(dataOutputStream, data);
            SerialUtil.writeMap(dataOutputStream, headers);
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Gzipper.zip(outputStream.toByteArray());
    }
}
