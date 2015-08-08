package com.crossbow.volley.toolbox;


import com.android.volley.RequestQueue;
import com.crossbow.volley.CrossbowImageCache;
import com.crossbow.volley.FileQueue;

public interface CrossbowComponents {

    RequestQueue provideRequestQueue();

    CrossbowImageCache provideImageCache();

    FileImageLoader provideFileImageLoader();

    FileQueue provideFileQueue();

    NetworkImageLoader provideImageLoader();
}
