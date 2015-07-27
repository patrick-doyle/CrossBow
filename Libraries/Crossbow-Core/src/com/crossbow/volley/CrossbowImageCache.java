package com.crossbow.volley;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Patrick on 27/07/2015.
 */
public interface CrossbowImageCache extends ImageLoader.ImageCache {

    /**
     * Reduce the cache size to reduce memory usage
     * @param level level from {@link android.content.ComponentCallbacks2#onTrimMemory(int)}
     */
    void trimMemory(int level);

    /**
     * Empty the cache and remove the bitmaps, <b>DONT RECYCLE THE BITMAPS</b>
     */
    void onLowMemory();
}
