package com.crossbow.volley.toolbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.widget.ImageView;

import com.android.volley.ParseError;
import com.android.volley.VolleyLog;
import com.crossbow.volley.BitmapScrapHeap;

import java.io.File;
import java.util.Locale;

/**
 * Copyright (C) 18/03/15 Patrick
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
 */
public class ImageDecoder {

    /**
     * Shared lock to prevent multiple images getting decoded at once and trowing an OOM error.
     */
    private final static Object DECODE_LOCK = new Object();

    /**
     * @see #parseImage(byte[], Bitmap.Config, android.widget.ImageView.ScaleType, int, int)
     */
    public static Bitmap parseImage(@NonNull byte[] data) throws ParseError {
        return parseImage(data, null, ImageView.ScaleType.CENTER_CROP, 0, 0);
    }

    /**
     * @see #parseImage(byte[], Bitmap.Config, android.widget.ImageView.ScaleType, int, int)
     */
    public static Bitmap parseImage(@NonNull byte[] data,  ImageView.ScaleType scaleType, int maxWidth, int maxHeight) throws ParseError {
        return parseImage(data, null, scaleType,  maxWidth, maxHeight);
    }

    /**
     * Decode a byte array into a bitmap, this will try its best to reuse old bitmaps allocations and will scale the image to the max height or width passed in.
     *
     * @param config Bitmap config for decode into, Defaults to RGB_565 if null is passed.
     * @param maxWidth maxWidth to decode to.
     * @param maxHeight maxHeight to decode to.
     * @return decoded bitmap or null if an error occured without a parse error.
     * @throws ParseError
     */
    public static Bitmap parseFile(@NonNull File file, @Nullable Bitmap.Config config, ImageView.ScaleType scaleType, int maxWidth, int maxHeight) throws ParseError {
        return parseImage(file, null, config, scaleType, maxWidth, maxHeight);
    }

    /**
     * Decode a byte array into a bitmap, this will try its best to reuse old bitmaps allocations and will scale the image to the max height or width passed in.
     *
     * @param maxWidth maxWidth to decode to.
     * @param maxHeight maxHeight to decode to.
     * @return decoded bitmap or null if an error occured without a parse error.
     * @throws ParseError
     */
    public static Bitmap parseFile(@NonNull File file, ImageView.ScaleType scaleType, int maxWidth, int maxHeight) throws ParseError {
        return parseImage(file, null, null, scaleType, maxWidth, maxHeight);
    }

    /**
     * Decode a byte array into a bitmap, this will try its best to reuse old bitmaps allocations and will scale the image to the max height or width passed in.
     *
     * @param data image to be decoded
     * @param config Bitmap config for decode into, Defaults to RGB_565 if null is passed.
     * @param maxWidth maxWidth to decode to.
     * @param maxHeight maxHeight to decode to.
     * @return decoded bitmap or null if an error occured without a parse error.
     * @throws ParseError
     */
    public static Bitmap parseImage(@NonNull byte[] data, @Nullable Bitmap.Config config, ImageView.ScaleType scaleType, int maxWidth, int maxHeight) throws ParseError {
        return parseImage(null, data, config, scaleType, maxWidth, maxHeight);
    }


    private static Bitmap parseImage(@Nullable File file, @Nullable byte[] data, @Nullable Bitmap.Config config, ImageView.ScaleType scaleType, int maxWidth, int maxHeight) throws ParseError {

        //Decode images one at a time, helps prevent OOMs
        synchronized (DECODE_LOCK) {

            BitmapScrapHeap bitmapScrapHeap = BitmapScrapHeap.get();

            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            Bitmap bitmap = null;

            if(config == null) {
                config = Bitmap.Config.RGB_565;
            }

            decodeOptions.inPreferredConfig = config;

            //If there is no limit on image size, do a plain decode
            if (maxWidth == 0 && maxHeight == 0) {
                bitmap = decodeBitmap(data, file, decodeOptions);
            }
            else {
                // If we have to resize this image, first get the natural bounds.
                decodeOptions.inJustDecodeBounds = true;
                decodeBitmap(data, file, decodeOptions);
                int actualWidth = decodeOptions.outWidth;
                int actualHeight = decodeOptions.outHeight;

                // Then compute the dimensions we would ideally like to decode to.
                int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight, scaleType);
                int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth, scaleType);

                if(desiredHeight <= 0 || desiredWidth <= 0) {
                    IllegalArgumentException exception = new IllegalArgumentException(String.format(Locale.ENGLISH, "Invalid image size, desiredHeight = %s, desiredHeight = %s", desiredHeight, desiredWidth));
                    throw new ParseError(exception);
                }

                // Use inPreferQualityOverSpeed where possible
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    decodeOptions.inPreferQualityOverSpeed = true;
                }

                // Decode to the nearest power of two scaling factor.
                decodeOptions.inJustDecodeBounds = false;
                decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);

                Bitmap tempBitmap;

                // Try to get a bitmap to reallocate
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                    decodeOptions.inMutable = true;

                    //not used in the decoding process,
                    //used for pool to figure out what the min size the bitmap needs to be
                    decodeOptions.outHeight = desiredHeight > actualHeight ? desiredHeight : actualHeight;
                    decodeOptions.outWidth = desiredWidth > actualWidth ? desiredWidth : actualWidth;

                    Bitmap recycled = null;

                    //Try to get a bitmap to reuse the allocation
                    recycled = bitmapScrapHeap.getBitmapToFill(decodeOptions);

                    if(recycled != null) {
                        decodeOptions.inBitmap = recycled;
                    }

                    try {
                        //try to decode into the old bitmap allocation
                        tempBitmap = decodeBitmap(data, file, decodeOptions);
                        if(decodeOptions.inBitmap != null) {
                            VolleyLog.d("Bitmap reuse");
                        }
                    }
                    catch (IllegalArgumentException e) {
                        //If decode fails, then create a new bitmap allocation
                        decodeOptions.inBitmap = null;
                        tempBitmap = decodeBitmap(data, file, decodeOptions);
                    }
                }
                else {
                    //Older platforms than HONEYCOMB cant reuse old allocations
                    tempBitmap = decodeBitmap(data, file, decodeOptions);
                }

                //If the bitmap is larger, scale down to the maximal acceptable size.
                if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {

                    bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);

                    //Only store if the bitmaps are not equal and the temp bitmap is ready to be reused/recycled
                    if (!tempBitmap.equals(bitmap)) {
                        //bitmaps can only be reallocated on newer than honeycomb,
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            bitmapScrapHeap.storeForReUse(tempBitmap);
                        }
                        else {
                            //older platforms need to recycle to prevent extra native heap allocations
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

    private static Bitmap decodeBitmap(byte[] imageData, File file, BitmapFactory.Options options) {
        if(imageData != null) {
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
        }

        if(file != null) {
            return BitmapFactory.decodeFile(file.getPath(), options);
        }

        return null;

    }

    /**
     * Copied from the ImageRequest method
     * Scales one side of a rectangle to fit aspect ratio.
     *
     * @param maxPrimary Maximum size of the primary dimension (i.e. width for
     *        max width), or zero to maintain aspect ratio with secondary
     *        dimension
     * @param maxSecondary Maximum size of the secondary dimension, or zero to
     *        maintain aspect ratio with primary dimension
     * @param actualPrimary Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     * @param scaleType The ScaleType used to calculate the needed image size.
     */
    @VisibleForTesting
    static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                           int actualSecondary, ImageView.ScaleType scaleType) {

        // If no dominant value at all, just return the actual.
        if ((maxPrimary == 0) && (maxSecondary == 0)) {
            return actualPrimary;
        }

        // If ScaleType.FIT_XY fill the whole rectangle, ignore ratio.
        if (scaleType == ImageView.ScaleType.FIT_XY) {
            if (maxPrimary == 0) {
                return actualPrimary;
            }
            return maxPrimary;
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

        // If ScaleType.CENTER_CROP fill the whole rectangle, preserve aspect ratio.
        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            if ((resized * ratio) < maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }

        if ((resized * ratio) > maxSecondary) {
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
