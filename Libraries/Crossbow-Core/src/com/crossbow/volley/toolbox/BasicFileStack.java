package com.crossbow.volley.toolbox;

import android.content.Context;
import android.content.res.AssetManager;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ByteArrayPool;
import com.crossbow.volley.FileRequest;
import com.crossbow.volley.FileResponse;
import com.crossbow.volley.FileStack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Patrick on 25/03/2015.
 */
public class BasicFileStack implements FileStack {

    private AssetManager assetManager;

    private static int DEFAULT_POOL_SIZE = 4096 * 2;

    private ByteArrayPool byteArrayPool = new ByteArrayPool(DEFAULT_POOL_SIZE);

    private final File assetCacheDir;
    private static final String ASSET_DIR = "CrossbowAssetCache";

    public BasicFileStack(Context context) {
        this.assetManager = context.getApplicationContext().getAssets();
        String cacheDir = context.getCacheDir() + File.separator + ASSET_DIR;
        this.assetCacheDir = new File(cacheDir);
        if(!assetCacheDir.exists()) {
            assetCacheDir.mkdirs();
        }
    }

    @Override
    public FileResponse performFileOperation(FileRequest fileRequest) throws VolleyError {

        try {
            //try to read from assets cache first
            File assetCachedFile = new File(assetCacheDir, fileRequest.getFilePath());
            if(assetCachedFile.exists()) {
                fileRequest.mark("Asset-cache-hit");
                return doFileRequestWork(fileRequest, assetCachedFile);
            }
            else {
                //copy the file from assets if it exists
                InputStream in = assetManager.open(fileRequest.getFilePath());
                File parentFileDir = assetCachedFile.getParentFile();
                if(parentFileDir != null && !parentFileDir.exists()) {
                    parentFileDir.mkdirs();
                }
                copyStreams(in, new FileOutputStream(assetCachedFile));
                fileRequest.mark("Asset-copy-to-cache");
                return doFileRequestWork(fileRequest, assetCachedFile);
            }
        }
        catch (IOException assetException) {
            fileRequest.mark("Asset-file-miss");
        }

        File file = new File(fileRequest.getFilePath());
        fileRequest.setByteArrayPool(byteArrayPool);
        return fileRequest.doFileWork(file);
    }

    private FileResponse doFileRequestWork(FileRequest fileRequest, File file) throws VolleyError {
        return fileRequest.doFileWork(file);
    }

    private void copyStreams(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = byteArrayPool.getBuf(1024);
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        inputStream.close();
    }
}
