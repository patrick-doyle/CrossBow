package com.crossbow.volley.toolbox;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.CrossbowImageCache;
import com.crossbow.volley.FileQueue;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

/**
 * Created by Patrick on 27/07/2015.
 */
public class CrossbowBuilder {

    private Context context;

    public RequestQueue requestQueue;

    public ImageLoader imageLoader;

    public CrossbowImageCache imageCache;

    public FileQueue fileQueue;

    public FileImageLoader fileImageLoader;

    private static final int DISK_CACHE_SIZE = 15 * 1024 * 1024;

    public CrossbowBuilder(Context context) {
        this.context = context.getApplicationContext();
        createComponents();
    }

    protected Context getContext() {
        return context;
    }

    private void createComponents() {

        OkHttpClient okHttpClient = createHttpClient();
        HttpStack stack = createHttpStack(okHttpClient);
        Network network = createNetwork(stack);
        Cache cache = createCache();
        imageCache = createImageCache();

        requestQueue = createQueue(cache, network);
        imageLoader = createImageLoader(requestQueue, imageCache);

        fileQueue = createFileQueue();
        fileImageLoader = createFileLoader(fileQueue, imageCache);

    }

    protected RequestQueue createQueue(Cache cache, Network network) {
        RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        return requestQueue;
    }

    public Cache createCache() {
        String cacheDir = getContext().getCacheDir() + File.separator + "CrossbowCache";
        File file = new File(cacheDir);
        return new DiskBasedCache(file, DISK_CACHE_SIZE);
    }

    public Network createNetwork(HttpStack httpStack) {
        return new BasicNetwork(httpStack);
    }

    public HttpStack createHttpStack(OkHttpClient okHttpClient) {
        return new OkHttpStack(okHttpClient);
    }

    public OkHttpClient createHttpClient() {
        return new OkHttpClient();
    }

    public CrossbowImageCache createImageCache() {
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        int memCacheSize = ((am.getMemoryClass() * 1024 * 1024) / 10);
        return new DefaultImageCache(memCacheSize);
    }

    public CrossbowImageLoader createImageLoader(RequestQueue requestQueue, CrossbowImageCache crossbowImageCache) {
        return new CrossbowImageLoader(requestQueue, crossbowImageCache);
    }

    public FileImageLoader createFileLoader(FileQueue requestQueue, CrossbowImageCache crossbowImageCache) {
        return new FileImageLoader(requestQueue, crossbowImageCache);
    }

    public FileQueue createFileQueue() {
        FileQueue fileQueue = new FileQueue(getContext());
        fileQueue.start();
        return fileQueue;
    }
}
