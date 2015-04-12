package com.crossbow.volley.toolbox;

import android.content.Context;
import android.content.res.AssetManager;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.PoolingByteArrayOutputStream;
import com.crossbow.volley.FileError;
import com.crossbow.volley.FileReadRequest;
import com.crossbow.volley.FileReadStreamRequest;
import com.crossbow.volley.FileStack;
import com.crossbow.volley.FileWriteRequest;
import com.crossbow.volley.FileWriteStreamRequest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by Patrick on 25/03/2015.
 */
public class BasicFileStack implements FileStack {
    private AssetManager assetManager;

    private static int DEFAULT_POOL_SIZE = 4096 * 2;
    private static int DEFAULT_BUFFER_SIZE = 4096;

    private ByteArrayPool byteArrayPool = new ByteArrayPool(DEFAULT_POOL_SIZE);

    public BasicFileStack(Context context) {
        this.assetManager = context.getApplicationContext().getAssets();
    }

    @Override
    public byte[] readFileData(FileReadRequest fileRequest) throws VolleyError {
        String filePath = fileRequest.getFilePath();
        File file = new File(filePath);

        PoolingByteArrayOutputStream poolingByteArrayOutputStream = new PoolingByteArrayOutputStream(byteArrayPool, (int) file.length());
        try {
            //got the input stream
            InputStream inputStream = openFileForRead(filePath);
            byte[] buffer = byteArrayPool.getBuf(DEFAULT_BUFFER_SIZE);

            int bytesRead = 0;
            while((bytesRead = inputStream.read(buffer)) != -1) {
                poolingByteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            byte[] data = poolingByteArrayOutputStream.toByteArray();
            poolingByteArrayOutputStream.close();
            inputStream.close();
            return data;
        }
        catch (Exception e) {
            throw new FileError(e);
        }
    }

    @Override
    public InputStream readFileStream(FileReadStreamRequest fileRequest) throws VolleyError {
        String filePath = fileRequest.getFilePath();
        try {
            return openFileForRead(filePath);
        }
        catch (Exception e) {
            throw new FileError(e);
        }
    }

    @Override
    public OutputStream writeFileStream(FileWriteStreamRequest fileRequest) throws VolleyError {
        String filePath = fileRequest.getFilePath();
        try {
            return openFileForWrite(filePath);
        }
        catch (Exception e) {
            throw new FileError(e);
        }
    }

    @Override
    public void writeFileData(FileWriteRequest fileRequest) throws VolleyError {
        byte[] fileData = fileRequest.getData();
        try {
            OutputStream outputStream = openFileForWrite(fileRequest.getFilePath());
            BufferedOutputStream bufferedOS = new BufferedOutputStream(outputStream);
            bufferedOS.write(fileData);
            bufferedOS.close();
        }
        catch (Exception e) {
            throw new FileError(e);
        }
    }

    private InputStream openFileForRead(String filePath) throws Exception {
        InputStream inputStream;
        File file = new File(filePath);

        if (!file.exists()) {
            //file not in a normal directory
            //try the assets, will throw if the file is missing
            inputStream = assetManager.open(filePath);
        } else {
            inputStream = new FileInputStream(file);
        }
        return inputStream;
    }

    private OutputStream openFileForWrite(String filePath) throws Exception {
        OutputStream outputStream;
        File file = new File(filePath);
        if (!file.exists()) {
            File parent = file.getParentFile();
            if(parent != null) {
                parent.mkdirs();
            }
            file.createNewFile();
        }
        outputStream = new FileOutputStream(file);
        return outputStream;
    }


}
