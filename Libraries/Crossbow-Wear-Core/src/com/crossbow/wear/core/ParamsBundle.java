package com.crossbow.wear.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Custom packing of transformer params to reduce the wearable to handheld
 * overhead from the bundle. Only supports primitives, string, charsequence and Serializable objects
 */
public class ParamsBundle {

    private Map<String, Object> internalMapping;

    public ParamsBundle() {
        this(8);
    }

    public ParamsBundle(int size) {
        internalMapping = new HashMap<>();
    }

    private ParamsBundle(Map<String, Object> map) {
        internalMapping = map;
    }

    public int getInt(String key, int defaultValue) {
        if(internalMapping.containsKey(key)) {
            return (int) internalMapping.get(key);
        }
        else {
            return defaultValue;
        }
    }

    public long getLong(String key, long defaultValue) {
        if(internalMapping.containsKey(key)) {
            return (long) internalMapping.get(key);
        }
        else {
            return defaultValue;
        }
    }

    public float getFloat(String key, float defaultValue) {
        if(internalMapping.containsKey(key)) {
            return (float) internalMapping.get(key);
        }
        else {
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        if(internalMapping.containsKey(key)) {
            return (double) internalMapping.get(key);
        }
        else {
            return defaultValue;
        }
    }

    public char getChar(String key, char defaultValue) {
        if(internalMapping.containsKey(key)) {
            return (char) internalMapping.get(key);
        }
        else {
            return defaultValue;
        }
    }


    public boolean getBoolean(String key, boolean defaultValue) {
        if(internalMapping.containsKey(key)) {
            return (boolean) internalMapping.get(key);
        }
        else {
            return defaultValue;
        }
    }

    public String getString(String key) {
        if(internalMapping.containsKey(key)) {
            return (String) internalMapping.get(key);
        }
        else {
            return null;
        }
    }

    public CharSequence getCharSequence(String key) {
        if(internalMapping.containsKey(key)) {
            return (CharSequence) internalMapping.get(key);
        }
        else {
            return null;
        }
    }

    public byte[] getByteArray(String key) {
        if(internalMapping.containsKey(key)) {
            return (byte[]) internalMapping.get(key);
        }
        else {
            return new byte[0];
        }
    }

    public int[] getIntArray(String key) {
        if(internalMapping.containsKey(key)) {
            return (int[]) internalMapping.get(key);
        }
        else {
            return new int[0];
        }
    }

    public long[] getLongArray(String key) {
        if(internalMapping.containsKey(key)) {
            return (long[]) internalMapping.get(key);
        }
        else {
            return new long[0];
        }
    }

    public float[] getFloatArray(String key) {
        if(internalMapping.containsKey(key)) {
            return (float[]) internalMapping.get(key);
        }
        else {
            return new float[0];
        }
    }

    public double[] getDoubleArray(String key) {
        if(internalMapping.containsKey(key)) {
            return (double[]) internalMapping.get(key);
        }
        else {
            return new double[0];
        }
    }

    public boolean[] getBooleanArray(String key) {
        if(internalMapping.containsKey(key)) {
            return (boolean[]) internalMapping.get(key);
        }
        else {
            return new boolean[0];
        }
    }

    public char[] getCharArray(String key) {
        if(internalMapping.containsKey(key)) {
            return (char[]) internalMapping.get(key);
        }
        else {
            return new char[0];
        }
    }

    public Serializable[] getSerializableArray(String key) {
        if(internalMapping.containsKey(key)) {
            return (Serializable[]) internalMapping.get(key);
        }
        else {
            return new Serializable[0];
        }
    }

    public Serializable getSerializable(String key) {
        if(internalMapping.containsKey(key)) {
            return (Serializable) internalMapping.get(key);
        }
        else {
            return null;
        }
    }

    public String[] getStringArray(String key) {
        if(internalMapping.containsKey(key)) {
            return (String[]) internalMapping.get(key);
        }
        else {
            return new String[0];
        }
    }

    public CharSequence[] getCharSequenceArray(String key) {
        if(internalMapping.containsKey(key)) {
            return (CharSequence[]) internalMapping.get(key);
        }
        else {
            return new CharSequence[0];
        }
    }

    public int size() {
        return internalMapping.size();
    }

    public Set<String> keySet() {
        return internalMapping.keySet();
    }


    public boolean containsKey(String key) {
        return internalMapping.containsKey(key);
    }

    public Object get(String key) {
        return internalMapping.get(key);
    }

    public void putInt(String key, int value) {
        internalMapping.put(key, value);
    }

    public void putLong(String key, long value) {
        internalMapping.put(key, value);
    }

    public void putFloat(String key, float value) {
        internalMapping.put(key, value);
    }

    public void putDouble(String key, double value) {
        internalMapping.put(key, value);
    }

    public void putChar(String key, char value) {
        internalMapping.put(key, value);
    }

    public void putBoolean(String key, boolean value) {
        internalMapping.put(key, value);
    }

    public void putString(String key, String value) {
        internalMapping.put(key, value);
    }

    public void putStringArray(String key, String[] value) {
        internalMapping.put(key, value);
    }

    public void putCharSequence(String key, CharSequence value) {
        internalMapping.put(key, value);
    }

    public void putCharSequenceArray(String key, CharSequence[] value) {
        internalMapping.put(key, value);
    }

    public void putByteArray(String key, byte[] value) {
        internalMapping.put(key, value);
    }

    public void putIntArray(String key, int[] value) {
        internalMapping.put(key, value);
    }

    public void putLongArray(String key, long[] value) {
        internalMapping.put(key, value);
    }

    public void putFloatArray(String key, float[] value) {
        internalMapping.put(key, value);
    }

    public void putDoubleArray(String key, double[] value) {
        internalMapping.put(key, value);
    }

    public void putCharArray(String key, char[] value) {
        internalMapping.put(key, value);
    }

    public void putBooleanArray(String key, boolean[] value) {
        internalMapping.put(key, value);
    }

    public void putSerializable(String key, Serializable value) {
        internalMapping.put(key, value);
    }

    public void putSerializableArray(String key, Serializable[] value) {
        internalMapping.put(key, value);
    }

    public void writeToStream(DataOutputStream dataStream) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(dataStream);
        objectOutputStream.writeObject(internalMapping);
    }

    public static ParamsBundle readFromStream(DataInputStream dataInputStream) throws IOException {
        ObjectInputStream objectInputStream = new ObjectInputStream(dataInputStream);
        try {
            Map<String, Object> map = (Map<String, Object>) objectInputStream.readObject();
            return new ParamsBundle(map);
        } catch (ClassNotFoundException e) {
           throw new IOException(e);
        }
    }
}
