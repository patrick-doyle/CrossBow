package com.crossbow.volley;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageLoader;

public abstract class CrossbowStack {

    private Context context;

    public CrossbowStack(Context context) {
        this.context = context.getApplicationContext();
    }

    protected final Context getContext() {
        return context;
    }

    public abstract @NonNull RequestQueue createRequestQueue();

    public abstract @NonNull ImageLoader createImageLoader(RequestQueue requestQueue, ImageLoader.ImageCache imageCache);

    public abstract @NonNull ImageLoader.ImageCache createImageCache();
}
