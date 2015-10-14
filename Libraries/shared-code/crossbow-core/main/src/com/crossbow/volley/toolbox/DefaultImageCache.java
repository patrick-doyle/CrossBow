package com.crossbow.volley.toolbox;

import android.content.ComponentCallbacks2;
import android.graphics.Bitmap;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.util.LruCache;

import com.crossbow.volley.CrossbowImageCache;

/*
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
 * Image cache used for the Image loader. Uses the {@link android.support.v4.util.LruCache} for storage.
 * This also supports trimming of memory from the {@link android.content.ComponentCallbacks2#onTrimMemory(int)} callback to
 * release bitmaps when the system runs low on memory.
 */
public class DefaultImageCache implements CrossbowImageCache {

    private LruCache<String, Bitmap> imageCache;

    /**
     * @param size - number of byes to use for the cache;
     */
    public DefaultImageCache(int size) {
        imageCache = new LruCache<String, Bitmap>(size){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return BitmapCompat.getAllocationByteCount(value);
            }
        };
    }

    @Override
    public synchronized Bitmap getBitmap(String url) {
        return imageCache.get(url);
    }

    @Override
    public synchronized void putBitmap(String url, Bitmap bitmap) {
        imageCache.put(url, bitmap);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void trimMemory(int level) {

        if(level >= ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            emptyCache(); //dump the cache
        }
        else if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE){
            trimCache(0.5f); // trim to half the max size
        }
        else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND){
            trimCache(0.7f); // trim to one seventh max size
        }
        else if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL){
            trimCache(0.2f); // trim to one fifth max size
        }
    }
    /**
     * @inheritDoc
     */
    @Override
    public void onLowMemory() {
        emptyCache();
    }

    private synchronized void trimCache(float sizeFraction) {
        imageCache.trimToSize((int) (imageCache.maxSize() * sizeFraction));
    }

    private synchronized void emptyCache() {
        imageCache.evictAll();
    }
}