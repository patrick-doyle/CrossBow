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
import com.crossbow.volley.HttpStackSelector;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

public class DefaultCrossbowComponents implements CrossbowComponents {

    private HttpStack httpStack;

    private Network network;

    private Cache cache;

    public CrossbowImageCache crossbowImageCache;

    public RequestQueue requestQueue;

    public NetworkImageLoader imageLoader;

    private static final int DISK_CACHE_SIZE = 15 * 1024 * 1024;

    private Context context;

    public DefaultCrossbowComponents(Context context) {
        this.context = context.getApplicationContext();
        httpStack = onCreateHttpStack();
        network = onCreateNetwork(httpStack);
        cache = onCreateCache();
        crossbowImageCache = onCreateImageCache();

        requestQueue = onCreateRequestQueue(cache, network);
        imageLoader = onCreateImageLoader(requestQueue, crossbowImageCache);
    }

    protected Context getContext() {
        return context;
    }

    @Override
    public RequestQueue provideRequestQueue() {
        return requestQueue;
    }

    @Override
    public CrossbowImageCache provideImageCache() {
        return crossbowImageCache;
    }
    @Override
    public NetworkImageLoader provideImageLoader() {
        return imageLoader;
    }

    @Override
    public Cache provideCache() {
        return cache;
    }

    @Override
    public Network provideNetwork() {
        return network;
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

    public Cache onCreateCache() {
        String cacheDir = getContext().getCacheDir() + File.separator + "CrossbowCache";
        File file = new File(cacheDir);
        return new DiskBasedCache(file, DISK_CACHE_SIZE);
    }

    public Network onCreateNetwork(HttpStack httpStack) {
        return new BasicNetwork(httpStack);
    }

    public HttpStack onCreateHttpStack() {
        return HttpStackSelector.createStack();
    }

}
