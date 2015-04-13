package com.crossbow.volley.toolbox;

import android.app.ActivityManager;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.support.annotation.NonNull;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.CrossbowStack;

import java.io.File;


public class DefaultCrossbowStack extends CrossbowStack {

    private static final int DISK_CACHE_SIZE = 5 * 1024 * 1024;

    private String cacheDir;
    private int memCacheSize;

    public DefaultCrossbowStack(Context context) {
        super(context);
        this.cacheDir = getContext().getCacheDir() + File.separator + "CrossbowCache";

        //Memory cache setup
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        this.memCacheSize = ((am.getMemoryClass() * 1024 * 1024)/10);
        this.memCacheSize = getImageCacheSize();
    }

    @Override
    public @NonNull RequestQueue createRequestQueue() {
        RequestQueue requestQueue = new RequestQueue(getHttpCache(), getNetwork(getHttpStack()));
        requestQueue.start();
        return requestQueue;
    }

    @Override
    public @NonNull ImageLoader createImageLoader(RequestQueue requestQueue, ImageLoader.ImageCache imageCache) {
        return new ImageLoader(requestQueue, imageCache);
    }

    @Override
    public @NonNull ImageLoader.ImageCache createImageCache() {
        return new CrossbowImageCache(memCacheSize);
    }

    protected Cache getHttpCache() {
        File file = new File(cacheDir);
        return new DiskBasedCache(file, DISK_CACHE_SIZE);
    }

    protected HttpStack getHttpStack() {
        HttpStack httpStack;

        String userAgent = "volley/0";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            httpStack = new HurlStack();
        }
        else {
            httpStack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
        }
        return httpStack;
    }

    protected Network getNetwork(HttpStack httpStack) {
        return new BasicNetwork(getHttpStack());
    }

    protected int getImageCacheSize() {
        return memCacheSize;
    }
}
