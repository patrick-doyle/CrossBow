package com.twist.volley;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Patrick on 10/08/2014.
 *
 * Provides the different components that volley uses. If you need to use a custom component you
 * should subclass the {@link com.twist.volley.toolbox.DefaultVolleyStack} class and override the method you want.
 * <br/>
 * <b>Warning</b>
 * Any implementations of this interface will be using in a singleton. Do not hold references to context in here or you will leak the context.
 * <br/>Use the get context method to prevent leaks
 */
public abstract class VolleyStack {

    private Context context;

    public VolleyStack(Context context) {
        this.context = context.getApplicationContext();
    }

    protected final Context getContext() {
        return context;
    }

    public abstract RequestQueue getRequestQueue();

    public abstract ImageLoader getImageLoader();

    public abstract ImageLoader.ImageCache getImageCache();

    public abstract Cache getHttpCache();

    public abstract HttpStack getHttpStack();

    public abstract Network getNetwork();

    public abstract int getImageCacheSize();
}
