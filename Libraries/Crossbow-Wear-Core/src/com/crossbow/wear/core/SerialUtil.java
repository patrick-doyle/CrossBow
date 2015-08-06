package com.crossbow.wear.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SerialUtil {

    public static void writeMap(DataOutputStream outputStream, Map<String, String> map) throws IOException {
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

    public static void writeBytes(DataOutputStream outputStream, byte[] data) throws IOException {
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
            String key = dataInputStream.readUTF();
            String value = dataInputStream.readUTF();
            map.put(key, value);
        }
        return map;
    }
}
