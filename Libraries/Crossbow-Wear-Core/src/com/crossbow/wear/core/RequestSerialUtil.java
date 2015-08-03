package com.crossbow.wear.core;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class used to flatten requests into a byte array for transmission over the wear apis
 */
public class RequestSerialUtil {

    public static byte[] serializeRequest(String uuid, Request<?> request) throws AuthFailureError, IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(500);
        DataOutputStream dataStream = new DataOutputStream(outputStream);

        //metadata first
        dataStream.writeUTF(uuid);

        //byte arrays
        writeBytes(dataStream, request.getBody());

        //Strings
        dataStream.writeUTF(request.getTag() != null ? request.getTag().toString() : "");
        dataStream.writeUTF(request.getUrl());
        dataStream.writeUTF(request.getBodyContentType());
        dataStream.writeUTF(request.getCacheKey());

        //enum
        dataStream.writeUTF(request.getPriority().name());

        //ints
        dataStream.writeInt(request.getTimeoutMs());
        dataStream.writeInt(request.getMethod());
        dataStream.writeInt(request.getRetryPolicy().getCurrentRetryCount());

        //map of headers
        writeMap(dataStream, request.getHeaders());

        //transformer key and bundle
        if(request instanceof WearRequest) {
            WearRequest wearRequest = WearRequest.class.cast(request);
            ParamsBundle bundle = wearRequest.getTransformerParams();
            String transformerKey = wearRequest.getTransFormerKey();
            if(bundle == null) {
                bundle = new ParamsBundle(0);
            }
            if(transformerKey == null) {
                transformerKey = "";
            }
            dataStream.writeUTF(transformerKey);
            bundle.writeToStream(dataStream);
        }
        else {
            dataStream.writeUTF("");
            new ParamsBundle(0).writeToStream(dataStream);
        }
        byte[] data = outputStream.toByteArray();
        dataStream.close();
        //gzip the data
        return Gzipper.zip(data);
    }

    public static WearDataRequest deSerializeRequest(byte[] request) throws IOException {
        //decompress the gzipped data
        byte[] deCompressed = Gzipper.unzip(request);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(deCompressed);
        DataInputStream dataStream = new DataInputStream(inputStream);

        //metadata
        String uuid = dataStream.readUTF();

        //byte arrays
        byte[] postBody = readBytes(dataStream);

        //Strings
        String tag = dataStream.readUTF();
        String url = dataStream.readUTF();
        String bodyType = dataStream.readUTF();
        String cacheKey = dataStream.readUTF();

        //Enum
        Request.Priority priority = Request.Priority.valueOf(dataStream.readUTF());

        //ints
        int timeout = dataStream.readInt();
        int method = dataStream.readInt();
        int retryies = dataStream.readInt();

        //headers
        Map<String, String> headers = readMap(dataStream);

        //transformer key, bundle
        String transformerKey = dataStream.readUTF();
        ParamsBundle transformerArgs = ParamsBundle.readFromStream(dataStream);
        dataStream.close();

        return new WearDataRequest(method, url, uuid, transformerKey,
                retryies, timeout, cacheKey, tag, bodyType,
                headers, postBody, priority, transformerArgs);
    }

    private static void writeMap(DataOutputStream outputStream, Map<String, String> map) throws IOException {
        //Write size, then key-values
        outputStream.writeInt(map.size());
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for(Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            outputStream.writeUTF(key);
            outputStream.writeUTF(value);
        }
    }

    private static void writeBytes(DataOutputStream outputStream, byte[] data) throws IOException {
        //Write size, then key-values
        if(data != null) {
            outputStream.writeInt(data.length);
            outputStream.write(data);
        }
        else {
            outputStream.writeInt(0);
            outputStream.write(new byte[0]);
        }
    }

    private static byte[] readBytes(DataInputStream outputStream) throws IOException {
        //Write size, then key-values
        int size = outputStream.readInt();
        byte[] data = new byte[size];
        outputStream.read(data);
        return data;
    }

    private static Map<String, String> readMap(DataInputStream dataInputStream) throws IOException {
        //Read size, then key-values in a loop
        int size = dataInputStream.readInt();
        Map<String, String> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            String key = dataInputStream.readUTF();
            String value = dataInputStream.readUTF();
            map.put(key, value);
        }
        return map;
    }


}
