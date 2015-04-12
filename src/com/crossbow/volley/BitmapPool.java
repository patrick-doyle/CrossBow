package com.crossbow.volley;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.graphics.BitmapCompat;
import android.util.Log;

import com.android.volley.VolleyLog;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Patrick on 19/03/15.
 */
public class BitmapPool {

    private static BitmapPool bitmapPool;

    private static final int UNUSED_BITMAP_COUNT = 50;

    private Set<WeakReference<Bitmap>> unusedBitmaps;

    public static BitmapPool get() {
        if(bitmapPool == null) {
            bitmapPool = new BitmapPool();
        }
        return bitmapPool;
    }

    private BitmapPool() {
        unusedBitmaps = Collections.newSetFromMap(new LinkedHashMap<WeakReference<Bitmap>, Boolean>(UNUSED_BITMAP_COUNT) {
            protected boolean removeEldestEntry(Map.Entry<WeakReference<Bitmap>, Boolean> eldest) {
                boolean shouldRemove = size() > UNUSED_BITMAP_COUNT;
                if (shouldRemove) {
                    VolleyLog.d("Pruning unused size = " + size());

                    Bitmap toBeScrapped = eldest.getKey().get();
                    if(toBeScrapped != null) {
                        toBeScrapped.recycle();
                    }
                }
                return shouldRemove;
            }
        });
    }

    public Bitmap getBitmapToFill(BitmapFactory.Options options) {
        synchronized (unusedBitmaps) {
            Iterator<WeakReference<Bitmap>> iterator = unusedBitmaps.iterator();

            while (iterator.hasNext()) {
                Bitmap bitmap = iterator.next().get();
                if(bitmap != null) {
                    if(bitmap.isMutable() && canUseForInBitmap(bitmap, options)) {
                        iterator.remove();
                        return bitmap;
                    }
                }
                else {
                    iterator.remove();
                }
            }
        }

        return null;
    }

    public  void storeForReUse(Bitmap bitmap) {
        synchronized(unusedBitmaps) {
            if(bitmapCanBeReused(bitmap)) {
                unusedBitmaps.add(new WeakReference<Bitmap>(bitmap));
            }
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
                return byteCount <= BitmapCompat.getAllocationByteCount(candidate);
            }
            final int  width = targetOptions.outWidth / targetOptions.inSampleSize;
            final int height = targetOptions.outHeight / targetOptions.inSampleSize;
            final int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= BitmapCompat.getAllocationByteCount(candidate);
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return candidate.getWidth() == targetOptions.outWidth && candidate.getHeight() == targetOptions.outHeight && targetOptions.inSampleSize == 1;
        }
        else {
            return false;
        }
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

    public void trim() {
        synchronized(unusedBitmaps) {
            Iterator<WeakReference<Bitmap>> iterator = unusedBitmaps.iterator();
            while (iterator.hasNext()) {
                WeakReference<Bitmap> reference = iterator.next();
                Bitmap bitmap = reference.get();
                if(bitmap != null) {
                    bitmap.recycle();
                }
                iterator.remove();
            }
        }
    }
}
