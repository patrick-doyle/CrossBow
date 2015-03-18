package com.crossbow.volley.toolbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.text.ParseException;
import java.util.Locale;

/**
 * Created by Patrick on 18/03/15.
 */
public class ImageDecoder {

    private final static Object decodeLock = new Object();

    public static Bitmap parseImage(@NonNull byte[] data) throws ParseError {
        return parseImage(data, null, null, 0, 0);
    }

    public static Bitmap parseImage(@NonNull byte[] data, int maxWidth, int maxHeight) throws ParseError {
        return parseImage(data, null, null, maxWidth, maxHeight);
    }

    public static Bitmap parseImage(@NonNull byte[] data, @Nullable Bitmap.Config config, int maxWidth, int maxHeight) throws ParseError {
        return parseImage(data, config, null, maxWidth, maxHeight);
    }

    public static Bitmap parseImage(@NonNull byte[] data, @Nullable Bitmap.Config config, @Nullable CrossbowImageCache crossbowImageCache, int maxWidth, int maxHeight) throws ParseError {

        //Decode images one at a time, prevents OOMs
        synchronized (decodeLock) {
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            Bitmap bitmap = null;
            if (maxWidth == 0 && maxHeight == 0) {
                decodeOptions.inPreferredConfig = config;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            } else {
                // If we have to resize this image, first get the natural bounds.
                decodeOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
                int actualWidth = decodeOptions.outWidth;
                int actualHeight = decodeOptions.outHeight;

                // Then compute the dimensions we would ideally like to decode to.
                int desiredWidth = getResizedDimension(maxWidth, maxHeight,actualWidth, actualHeight);
                int desiredHeight = getResizedDimension(maxHeight, maxWidth,actualHeight, actualWidth);

                if(desiredHeight <= 0 || desiredWidth <= 0) {
                    IllegalArgumentException exception = new IllegalArgumentException(String.format(Locale.ENGLISH, "Invalid image size, desiredHeight = %s, desiredHeight = %s", desiredHeight, desiredWidth));
                    throw new ParseError(exception);
                }

                // Decode to the nearest power of two scaling factor.

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    decodeOptions.inPreferQualityOverSpeed = true;
                }

                decodeOptions.inJustDecodeBounds = false;
                decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);

                Bitmap tempBitmap;

                // Try to get a bitmap to decode into
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                    decodeOptions.inMutable = true;
                    decodeOptions.outHeight = desiredHeight > actualHeight ? desiredHeight : actualHeight;
                    decodeOptions.outWidth = desiredWidth > actualWidth ? desiredWidth : actualWidth;

                    Bitmap recycled = null;

                    if(crossbowImageCache != null) {
                        recycled = crossbowImageCache.getBitmapToFill(decodeOptions);
                    }

                    if(recycled != null) {
                        decodeOptions.inBitmap = recycled;
                    }

                    try {
                        tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
                    }
                    catch (IllegalArgumentException e) {
                        decodeOptions.inBitmap = null;
                        tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
                    }
                }
                else {
                    tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
                }

                // If the bitmap is larger, scale down to the maximal acceptable size.
                if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {

                    bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
                    //Only store if the bitmaps are not equal and the temp bitmap is ready to be reused
                    if (!tempBitmap.equals(bitmap)) {
                        //bitmaps can only be reallocated on newer than honeycomb,
                        //older platforms need to recycle to prevent extra native heap allocations
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            if(crossbowImageCache != null) {
                                crossbowImageCache.storeForReUse(tempBitmap);
                            }
                        }
                        else {
                            tempBitmap.recycle();
                        }
                    }
                }
                else {
                    bitmap = tempBitmap;
                }
            }

            return bitmap;
        }
    }

    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    private static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }
}
