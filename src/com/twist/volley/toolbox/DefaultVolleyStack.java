package com.twist.volley.toolbox;

import android.app.ActivityManager;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.twist.volley.VolleyStack;

import java.io.File;

/**
 * Created by Patrick on 10/08/2014.
 *
 * Normal implementation of the Volley stack
 */
public class DefaultVolleyStack extends VolleyStack {

    private static final int DISK_CACHE_SIZE = 5 * 1024 * 1024;

    private String cacheDir;
    private int memCacheSize;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    public DefaultVolleyStack(Context context) {
        super(context);
        this.cacheDir = getContext().getCacheDir() + File.separator + "Volley";

        //Memory cache setup
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        this.memCacheSize = ((am.getMemoryClass() * 1024 * 1024)/12);
        this.memCacheSize = getImageCacheSize();

        //Queue setUp
        requestQueue = getRequestQueue();
        imageLoader = new ImageLoader(requestQueue, getImageCache());
    }

    @Override
    public RequestQueue getRequestQueue() {
        requestQueue = new RequestQueue(getHttpCache(), getNetwork());
        requestQueue.start();
        return requestQueue;
    }

    @Override
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    @Override
    public ImageLoader.ImageCache getImageCache() {
        return new ImageMemCache(memCacheSize);
    }

    @Override
    public Cache getHttpCache() {
        File file = new File(cacheDir);
        return new DiskBasedCache(file, DISK_CACHE_SIZE);
    }

    @Override
    public HttpStack getHttpStack() {
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

    @Override
    public Network getNetwork() {
        return new BasicNetwork(getHttpStack());
    }

    @Override
    public int getImageCacheSize() {
        return memCacheSize;
    }
}
