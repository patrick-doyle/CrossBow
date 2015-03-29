package com.crossbow.volley.toolbox;

import android.content.Context;
import android.content.res.AssetManager;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.PoolingByteArrayOutputStream;
import com.crossbow.volley.FileError;
import com.crossbow.volley.FileReader;
import com.crossbow.volley.FileRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Patrick on 25/03/2015.
 */
public class BasicFileReader implements FileReader {
    private AssetManager assetManager;

    private static int DEFAULT_POOL_SIZE = 4096 * 2;
    private static int DEFAULT_BUFFER_SIZE = 4096;

    private ByteArrayPool byteArrayPool = new ByteArrayPool(DEFAULT_POOL_SIZE);

    public BasicFileReader(Context context) {
        this.assetManager = context.getApplicationContext().getAssets();
    }

    @Override
    public byte[] readFile(FileRequest fileRequest) throws VolleyError {

        String filePath = fileRequest.getFilePath();
        File file = new File(filePath);

        PoolingByteArrayOutputStream poolingByteArrayOutputStream = new PoolingByteArrayOutputStream(byteArrayPool, (int) file.length());
        try {
            InputStream inputStream;

            if (!file.exists()) {
                //file not in a normal directory
                //try the assets, will throw if the file is missing
                inputStream = assetManager.open(filePath);
            }
            else {
                inputStream = new FileInputStream(file);
            }

            //got the input stream
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
}
