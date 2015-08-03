package com.crossbow.wear.core;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public class ParamsBundleTest extends TestCase {

    @SmallTest
    public void testParamsBundleTest() throws IOException {
        ParamsBundle paramsBundle = new ParamsBundle();
        paramsBundle.putInt("1", 2);
        paramsBundle.putLong("2", 4l);
        paramsBundle.putDouble("3", 6d);
        paramsBundle.putFloat("4", 7f);
        paramsBundle.putBoolean("5", true);
        paramsBundle.putSerializable("6", "test-serializable");
        paramsBundle.putChar("7", 't');
        paramsBundle.putString("8", "putString");
        paramsBundle.putCharSequence("9", "putCharSequence");

        paramsBundle.putCharArray("10", new char[]{'t', 'u'});
        paramsBundle.putIntArray("11", new int[]{87645, 257527});
        paramsBundle.putLongArray("12", new long[]{858, 942});
        paramsBundle.putDoubleArray("13", new double[]{12d, 3.14d});
        paramsBundle.putFloatArray("14", new float[]{3f, 8f});
        paramsBundle.putByteArray("15", new byte[]{0x34, 0x54});
        paramsBundle.putBooleanArray("16", new boolean[]{true, false});
        paramsBundle.putStringArray("17", new String[]{"String", "String-String"});
        paramsBundle.putCharSequenceArray("18", new CharSequence[]{"CharSequence", "CharSequence-String"});
        paramsBundle.putSerializableArray("19", new Serializable[]{"Serializable", "Serializable-String"});

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);

       // paramsBundle.writeToStream(outputStream);
        outputStream.close();

        byte[] data = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        ParamsBundle deserialzed = new ParamsBundle();//ParamsBundle.readFromStream(dataInputStream);
        dataInputStream.close();

        assertEquals(2, deserialzed.getInt("1", 0));
        assertEquals(4l, deserialzed.getLong("2", 0));
        assertEquals(6d, deserialzed.getDouble("3", 0));
        assertEquals(7f, deserialzed.getFloat("4", 0));
        assertEquals(true, deserialzed.getBoolean("5", false));
        assertEquals("test-serializable", deserialzed.getSerializable("6"));
        assertEquals('t', deserialzed.getChar("7", 'w'));
        assertEquals("putString", deserialzed.getString("8"));
        assertEquals("putCharSequence", deserialzed.getCharSequence("9"));

        assertTrue(Arrays.equals(new char[]{'t', 'u'}, deserialzed.getCharArray("10")));
        assertTrue(Arrays.equals(new int[]{87645, 257527}, deserialzed.getIntArray("11")));
        assertTrue(Arrays.equals(new long[]{858, 942}, deserialzed.getLongArray("12")));
        assertTrue(Arrays.equals(new double[]{12d, 3.14d}, deserialzed.getDoubleArray("13")));
        assertTrue(Arrays.equals(new float[]{3f, 8f}, deserialzed.getFloatArray("14")));
        assertTrue(Arrays.equals(new byte[]{0x34, 0x54}, deserialzed.getByteArray("15")));
        assertTrue(Arrays.equals(new boolean[]{true, false}, deserialzed.getBooleanArray("16")));
        assertTrue(Arrays.equals(new String[]{"String", "String-String"}, deserialzed.getStringArray("17")));
        assertTrue(Arrays.equals(new CharSequence[]{"CharSequence", "CharSequence-String"}, deserialzed.getCharSequenceArray("18")));
        assertTrue(Arrays.equals(new Serializable[]{"Serializable", "Serializable-String"}, deserialzed.getSerializableArray("19")));
    }
}
