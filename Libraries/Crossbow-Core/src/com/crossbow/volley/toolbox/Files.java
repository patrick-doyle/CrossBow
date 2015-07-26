package com.crossbow.volley.toolbox;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Patrick on 07/01/15.
 */
public class Files {

    /**
     * Copies a file then
     * @return true if the file was copied
     */
    public static void copyFile(File from, File to) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(from);
        FileOutputStream fileOutputStream = new FileOutputStream(to);

        byte[] buffer = new byte[2048];
        int read = 0;
        while((read = fileInputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, read);
        }
        fileInputStream.close();
        fileOutputStream.close();
    }

    /**
     * Copies a file then deletes the source
     * @return true if the file was copied and deleted
     */
    public static boolean moveFile(File from, File to) throws IOException {
        return from.renameTo(to);
    }

    /**
     * Reads a file to a byte arrray
     * @param  file file to read
     */
    public static byte[] readFileData(File file) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        FileInputStream fileInputStream = new FileInputStream(file);

        byte[] buffer = new byte[2048];
        int read = 0;
        while((read = fileInputStream.read(buffer)) > 0) {
            byteBuffer.put(buffer, 0, read);
        }
        fileInputStream.close();
        return byteBuffer.array();
    }

    /**
     * Reads a file from assets to a byte array
     * @param  name file to read
     */
    public static byte[] readAssetData(Context context, String name) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = context.getApplicationContext().getAssets().open(name);

        int size = 0;

        byte[] buffer = new byte[2048];
        while((size = inputStream.read(buffer)) >= 0){
            outputStream.write(buffer,0,size);
        }

        inputStream.close();
        byte[] data = outputStream.toByteArray();
        outputStream.close();
        return data;
    }

    /**
     * Reads a file from assets to a byte array
     * @param  name file to read
     */
    public static boolean copyFileFromAssets(Context context, String name, File output) throws IOException {

        FileOutputStream outputStream = new FileOutputStream(output);
        InputStream inputStream = context.getApplicationContext().getAssets().open(name);

        int size = 0;

        byte[] buffer = new byte[2048];
        while((size = inputStream.read(buffer)) >= 0){
            outputStream.write(buffer,0,size);
        }

        inputStream.close();
        outputStream.close();
        return true;
    }

    /**
     * Reads a file from assets to a string
     * @param  name file to read
     */
    public static String readAssetString(Context context, String name) throws IOException {
        return new String(readAssetData(context, name));
    }

    /**
     * Reads a file to a string
     * @param  file file to read
     */
    public static String readFileString(File file) throws IOException {
        return new String(readFileData(file));
    }

    /**
     * Reads a file line by line to a list of Strings
     * @param file file to read
     */
    public static List<String> readFileLines(File file) throws IOException {
        List<String> strings = new ArrayList<>();
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        while((line = bufferedReader.readLine()) != null) {
            strings.add(line);
        }
        fileReader.close();
        bufferedReader.close();
        return strings;
    }

    /**
     * Reads a file line by line to a list of Strings
     * @param name file to read
     */
    public static List<String> readAssetLines(Context context, String name) throws IOException {
        List<String> strings = new ArrayList<>();
        InputStream inputStream = context.getAssets().open(name);
        InputStreamReader fileReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        while((line = bufferedReader.readLine()) != null) {
            strings.add(line);
        }
        fileReader.close();
        bufferedReader.close();
        return strings;
    }

    /**
     * writes a file line by line to a list of Strings
     * @param file file to read
     */
    public static void writeFileLines(File file, Collection<String> strings) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for(String line : strings) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }

    /**
     * writes a file line by line to a list of Strings
     * @param file file to read
     */
    public static void writeFileLines(File file, String[] strings) throws IOException {
        writeFileLines(file, Arrays.asList(strings));
    }

    /**
     * writes a file line by line to a list of Strings
     * @param file file to read
     */
    public static void writeFileString(File file, String string) throws IOException {
        writeFileData(file, string.getBytes());
    }

    /**
     * writes a byte[] to a file
     * @param file file to read
     */
    public static void writeFileData(File file, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
    }

    private static String getStringExeption(Exception e) {
        return e != null ? e.toString() : "";
    }
}
