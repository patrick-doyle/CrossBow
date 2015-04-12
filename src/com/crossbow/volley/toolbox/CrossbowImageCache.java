package com.crossbow.volley.toolbox;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

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
 * Image cache used for the Image loader. This will also get the least recently used
 * bitmap and try use that to decode new bitmaps in the image request.
 */
public class CrossbowImageCache implements ImageLoader.ImageCache {

    private LruCache<String, Bitmap> imageCache;

    /**
     * @param size - number of byes to use for the cache;
     */
    protected CrossbowImageCache(int size) {
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
    public synchronized Bitmap getBitmap(String url) {
        return imageCache.get(url);
    }

    @Override
    public synchronized void putBitmap(String url, Bitmap bitmap) {
        imageCache.put(url, bitmap);
    }


}