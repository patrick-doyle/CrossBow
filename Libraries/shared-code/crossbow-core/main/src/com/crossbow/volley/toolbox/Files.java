package com.crossbow.volley.toolbox;

import android.content.Context;

import com.crossbow.volley.FileError;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
 * Utility class used to move files around in a file request, throws {@link FileError} instead of
 * IOException for easier interop with crossbow requests.
 */
public class Files {

    /**
     * Reads a file to a byte arrray
     * @param  file file to read
     */
    public static byte[] readFileData(File file) throws FileError {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            byte[] buffer = new byte[2048];
            int read = 0;
            while((read = fileInputStream.read(buffer)) > 0) {
                byteBuffer.put(buffer, 0, read);
            }
            bufferedInputStream.close();
            return byteBuffer.array();
        } catch (IOException e) {
            throw new FileError(e);
        }
    }

    /**
     * Reads a file from assets to a byte array
     */
    public static byte[] readInputStreamData(InputStream inputStream) throws FileError {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            int size = 0;

            byte[] buffer = new byte[2048];
            while((size = inputStream.read(buffer)) >= 0){
                outputStream.write(buffer,0,size);
            }

            inputStream.close();
            byte[] data = outputStream.toByteArray();
            outputStream.close();
            return data;
        } catch (IOException e) {
            throw new FileError(e);
        }
    }

    /**
     * Reads a file from assets to a byte array
     */
    public static String readInputStreamString(InputStream inputStream) throws FileError {
        return new String(readInputStreamData(inputStream));
    }

    /**
     * Reads a file from assets to a byte array
     */
    public static boolean copyFileFromStream(InputStream inputStream, File output) throws FileError {

        try {
            FileOutputStream outputStream = new FileOutputStream(output);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            int size = 0;

            byte[] buffer = new byte[2048];
            while((size = inputStream.read(buffer)) >= 0){
                bufferedOutputStream.write(buffer,0,size);
            }

            inputStream.close();
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        } catch (IOException e) {
            throw new FileError(e);
        }
        return true;
    }

    /**
     * Reads a file to a string
     * @param  file file to read
     */
    public static String readFileString(File file) throws FileError {
        return new String(readFileData(file));
    }

    /**
     * Reads a file line by line to a list of Strings
     * @param file file to read
     */
    public static List<String> readFileLines(File file) throws FileError {
        try {
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
        } catch (IOException e) {
            throw new FileError(e);
        }
    }

    /**
     * writes a file line by line to a list of Strings
     * @param file file to read
     */
    public static void writeFileLines(File file, Collection<String> strings) throws FileError {
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for(String line : strings) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            throw new FileError(e);
        }

    }

    /**
     * writes a file line by line to a list of Strings
     * @param file file to read
     */
    public static void writeFileLines(File file, String[] strings) throws FileError {
        writeFileLines(file, Arrays.asList(strings));
    }

    /**
     * writes a file line by line to a list of Strings
     * @param file file to read
     */
    public static void writeFileString(File file, String string) throws FileError {
        writeFileData(file, string.getBytes());
    }

    /**
     * writes a byte[] to a file
     * @param file file to read
     */
    public static void writeFileData(File file, byte[] data) throws FileError {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        }
        catch (IOException e) {
            throw new FileError(e);
        }
    }
}
