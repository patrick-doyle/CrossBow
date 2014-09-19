package com.twist.volley;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageLoader;

/**
 * Copyright (C) 2014 Patrick Doyle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Provides the different components that volley uses. If you need to use a custom component you
 * should subclass the {@link com.twist.volley.toolbox.DefaultVolleyStack} class and override the method you want.
 * <br/>
 * <b>Warning</b>
 * Any implementations of this interface will be using in a singleton. Do not hold references to context in here or you will leak the context.
 * <br/>Use the {@link #getContext()} method to prevent leaks
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
