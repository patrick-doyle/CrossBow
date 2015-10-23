package com.crossbow.volley.toolbox;


import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.crossbow.volley.CrossbowImageCache;
import com.crossbow.volley.FileQueue;

public interface CrossbowComponents {

    RequestQueue provideRequestQueue();

    CrossbowImageCache provideImageCache();

    NetworkImageLoader provideImageLoader();

    Cache provideCache();

    Network provideNetwork();
}
