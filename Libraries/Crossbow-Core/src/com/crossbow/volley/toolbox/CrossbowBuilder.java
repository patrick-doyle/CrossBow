package com.crossbow.volley.toolbox;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.crossbow.volley.CrossbowImageCache;
import com.crossbow.volley.FileQueue;

/**
 * Created by Patrick on 27/07/2015.
 */
public abstract class CrossbowBuilder {

    private Context context;

    public CrossbowBuilder(Context context) {
        this.context = context.getApplicationContext();
    }

    protected Context getContext() {
        return context;
    }

    public abstract RequestQueue getRequestQueue();

    public abstract CrossbowImageCache getImageCache();

    public abstract FileImageLoader getFileImageLoader();

    public abstract FileQueue getFileQueue();

    public abstract NetworkImageLoader getImageLoader();
}
