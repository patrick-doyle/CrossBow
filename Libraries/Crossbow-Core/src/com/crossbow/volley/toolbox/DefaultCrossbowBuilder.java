package com.crossbow.volley.toolbox;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.crossbow.volley.CrossbowImageCache;
import com.crossbow.volley.FileQueue;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

/**

 */
public class DefaultCrossbowBuilder extends CrossbowBuilder {

    private OkHttpClient okHttpClient;

    private HttpStack httpStack;

    private Network network;

    private Cache cache;

    public CrossbowImageCache crossbowImageCache;

    public FileQueue fileQueue;

    public FileImageLoader fileImageLoader;

    public RequestQueue requestQueue;

    public NetworkImageLoader imageLoader;


    private static final int DISK_CACHE_SIZE = 15 * 1024 * 1024;

    public DefaultCrossbowBuilder(Context context) {
        super(context);
        okHttpClient = onCreateHttpClient();
        httpStack = onCreateHttpStack(okHttpClient);
        network = onCreateNetwork(httpStack);
        cache = onCreateCache();
        crossbowImageCache = onCreateImageCache();

        fileQueue = onCreateFileQueue();
        requestQueue = onCreateRequestQueue(cache, network);

        fileImageLoader = onCreateFileImageLoader(fileQueue, crossbowImageCache);
        imageLoader = onCreateImageLoader(requestQueue, crossbowImageCache);
    }

    @Override
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    @Override
    public CrossbowImageCache getImageCache() {
        return crossbowImageCache;
    }

    @Override
    public FileImageLoader getFileImageLoader() {
        return fileImageLoader;
    }

    @Override
    public FileQueue getFileQueue() {
        return fileQueue;
    }

    @Override
    public NetworkImageLoader getImageLoader() {
        return imageLoader;
    }

    public RequestQueue onCreateRequestQueue(Cache cache, Network network) {
        RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        return requestQueue;
    }

    public CrossbowImageCache onCreateImageCache() {
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        int memCacheSize = ((am.getMemoryClass() * 1024 * 1024) / 10);
        return new DefaultImageCache(memCacheSize);
    }

    public NetworkImageLoader onCreateImageLoader(RequestQueue requestQueue, CrossbowImageCache crossbowImageCache) {
        return new NetworkImageLoader(requestQueue, crossbowImageCache);
    }

    public FileQueue onCreateFileQueue() {
        FileQueue fileQueue = new FileQueue(getContext());
        fileQueue.start();
        return fileQueue;
    }

    public FileImageLoader onCreateFileImageLoader(FileQueue fileQueue, CrossbowImageCache imageCache) {
        return new FileImageLoader(fileQueue, imageCache);
    }

    public Cache onCreateCache() {
        String cacheDir = getContext().getCacheDir() + File.separator + "CrossbowCache";
        File file = new File(cacheDir);
        return new DiskBasedCache(file, DISK_CACHE_SIZE);
    }

    public Network onCreateNetwork(HttpStack httpStack) {
        return new BasicNetwork(httpStack);
    }

    public HttpStack onCreateHttpStack(OkHttpClient okHttpClient) {
        return new OkHttpStack(okHttpClient);
    }

    public OkHttpClient onCreateHttpClient() {
        return new OkHttpClient();
    }

}
