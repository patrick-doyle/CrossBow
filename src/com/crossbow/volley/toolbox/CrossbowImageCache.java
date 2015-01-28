package com.crossbow.volley.toolbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.android.volley.toolbox.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    private Set<WeakReference<Bitmap>> unusedBitmaps =  Collections.synchronizedSet(new HashSet<WeakReference<Bitmap>>(100));

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

    public synchronized Bitmap getBitmapToFill(BitmapFactory.Options options) {
        for(WeakReference<Bitmap> bitmapSoftReference : unusedBitmaps) {
            if(bitmapSoftReference.get() != null) {
                Bitmap bitmap = bitmapSoftReference.get();
                if(bitmap.isMutable() && canUseForInBitmap(bitmap, options)) {
                    unusedBitmaps.remove(bitmapSoftReference);
                    return bitmap;
                }
            }
        }
        return null;
    }

    public synchronized void storeForReUse(Bitmap bitmap) {
        if(bitmapCanBeReused(bitmap)) {
            unusedBitmaps.add(new WeakReference<Bitmap>(bitmap));
        }
    }

    private static boolean bitmapCanBeReused(Bitmap bitmap) {
        return bitmap != null && !bitmap.isRecycled() && bitmap.isMutable();
    }

    private static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if(targetOptions.inSampleSize == 0) {
                final int width = targetOptions.outWidth;
                final int height = targetOptions.outHeight;
                final int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
                return byteCount <= candidate.getAllocationByteCount();
            }
            final int  width = targetOptions.outWidth / targetOptions.inSampleSize;
            final int height = targetOptions.outHeight / targetOptions.inSampleSize;
            final int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        }

        return candidate.getWidth() == targetOptions.outWidth && candidate.getHeight() == targetOptions.outHeight && targetOptions.inSampleSize == 1;
    }

    private static int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        }
        else if (config == Bitmap.Config.RGB_565) {
            return 2;
        }
        else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        }
        else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }
}
