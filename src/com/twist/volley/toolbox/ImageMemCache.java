package com.twist.volley.toolbox;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Patrick on 04/08/2014.
 */
public class ImageMemCache implements ImageLoader.ImageCache {

    private LruCache<String, Bitmap> imageCache;

    /**
     * @param size - number of byes to use for the cache;
     */
    protected ImageMemCache(int size) {
        imageCache = new LruCache<String, Bitmap>(size){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return value.getAllocationByteCount();
                }
                else {
                    return value.getRowBytes() * value.getHeight();
                }
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return imageCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        imageCache.put(url, bitmap);
    }
}
