package com.crossbow.wear.core;

import android.support.annotation.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SerialUtil {

    public static void writeMap(DataOutputStream outputStream,  @Nullable Map<String, String> map) throws IOException {
        if(map == null) {
            map = Collections.emptyMap();
        }
        //Write size, then key-values
        outputStream.writeInt(map.size());
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for(Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            writeString(outputStream, key);
            writeString(outputStream, value);
        }
    }

    public static void writeString(DataOutputStream outputStream, @Nullable String data) throws IOException {
        if(data == null) {
            data = "";
        }
        outputStream.writeInt(data.getBytes().length);
        outputStream.write(data.getBytes());
    }

    public static String readString(DataInputStream inputStream) throws IOException {
        int length = inputStream.readInt();
        byte[] data = new byte[length];
        inputStream.read(data);
        return new String(data);
    }

    public static void writeBytes(DataOutputStream outputStream, @Nullable byte[] data) throws IOException {
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

    public static byte[] readBytes(DataInputStream outputStream) throws IOException {
        //Read size, then key-values
        int size = outputStream.readInt();
        byte[] data = new byte[size];
        outputStream.read(data, 0, size);
        return data;
    }

    public static Map<String, String> readMap(DataInputStream dataInputStream) throws IOException {
        //Read size, then key-values in a loop
        int size = dataInputStream.readInt();
        Map<String, String> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            String key = readString(dataInputStream);
            String value = readString(dataInputStream);
            map.put(key, value);
        }
        return map;
    }
}
