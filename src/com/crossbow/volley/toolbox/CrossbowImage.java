package com.crossbow.volley.toolbox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.ImageLoad;

import java.util.Deque;
import java.util.LinkedList;
import java.util.WeakHashMap;

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
 */
public class CrossbowImage {

    private static CrossbowImage crossbowImage;

    /**
     * Get the instance of CrossbowImage,
     * this will also create an new internal ImageLoad to to store the load params
     *
     * @param context An application context
     * @return the current instance of the CrossbowImage
     */
    public static CrossbowImage from(Context context) {
        if(crossbowImage == null) {
            crossbowImage = new CrossbowImage(context.getApplicationContext());
        }
        crossbowImage.newLoad();
        return crossbowImage;
    }

    private ImageLoader imageLoader;
    private FileImageLoader fileImageLoader;
    private Crossbow crossbow;
    private WeakHashMap<ImageView, ImageLoad> loadMap = new WeakHashMap<ImageView, ImageLoad>();
    private Deque<ImageLoad.Builder> builderScrap = new LinkedList<>();
    private ImageLoad.Builder builder;

    private CrossbowImage(Context context) {
        crossbow = Crossbow.get(context);
        imageLoader = crossbow.getImageLoader();
        fileImageLoader = crossbow.getFileImageLoader();
    }

    private void newLoad() {
        if(!builderScrap.isEmpty()) {
            builder = builderScrap.pop();
            builder.reset(imageLoader, fileImageLoader);
        }
        else {
            builder = new ImageLoad.Builder(imageLoader, fileImageLoader);
        }
    }

    /**
     * Sets the url to load
     *
     * @param url the url for the image
     */
    public CrossbowImage url(String url) {
        builder.url(url);
        return this;
    }

    /**
     * Sets the file to load
     *
     * @param file the filePath for the image
     */
    public CrossbowImage file(String file) {
        builder.file(file);
        return this;
    }

    /**
     * Sets the drawable resource
     *
     * @param drawable the resource id of thr drawable to load
     */
    public CrossbowImage drawable(int drawable) {
        builder.drawable(drawable);
        return this;
    }

    /**
     * Sets the fade in duration for when the image loads.
     *
     * @param duration the duration to fade
     */
    public CrossbowImage fade(int duration) {
        builder.fade(duration);
        return this;
    }

    /**
     * The drawable res to use when the image fails to load
     *
     * @param errorRes the drawable res to use
     */
    public CrossbowImage error(@DrawableRes int errorRes) {
        builder.errorRes(errorRes);
        return this;
    }

    /**
     * The drawable res to show while the image loads.
     *
     * @param defaultRes the drawable res to use
     */
    public CrossbowImage defaultRes(@DrawableRes int defaultRes) {
        builder.defaultRes(defaultRes);
        return this;
    }

    /**
     * Sets the image scaletype to center crop when the image loads
     */
    public CrossbowImage centerCrop() {
        return scale(ImageView.ScaleType.CENTER_CROP);
    }

    /**
     * Sets the scale type for the default/placeholder image.
     *
     * @param scaleType scaletype to set.
     */
    public CrossbowImage placeHolderScale(ImageView.ScaleType scaleType) {
        builder.preScaleType(scaleType);
        return this;
    }

    /**
     * Sets the scale type for the error image image.
     *
     * @param errorScaleType scaletype to set.
     */
    public CrossbowImage errorScale(ImageView.ScaleType errorScaleType) {
        builder.errorScaleType(errorScaleType);
        return this;
    }

    /**
     * Prevent the Imagefrom getting scaled at all. <b>Will cause the image to load the full size image into the cache</b>
     *
     */
    public CrossbowImage dontScale() {
        builder.dontScale(true);
        return this;
    }

    /**
     * Sets the image scaletype to fit center when the image loads
     */
    public CrossbowImage fitInto() {
        return scale(ImageView.ScaleType.FIT_CENTER);
    }

    /**
     * Sets the image scaletype to fit center when the image loads
     */
    public CrossbowImage listen(Listener listener) {
        builder.listen(listener);
        return this;
    }

    /**
     * Sets the image scaletype to this when the image loads
     *
     * @param scaleType the scaletype to set.
     */
    public CrossbowImage scale(ImageView.ScaleType scaleType) {
        builder.scaleType(scaleType);
        return this;
    }

    public CrossbowImage dontClear() {
        builder.dontClear(true);
        return this;
    }

    public void into(View root, @IdRes int imageViewId) {
        ImageView imageView = (ImageView) root.findViewById(imageViewId);
        into(imageView);
    }

    public void into(Activity activty, @IdRes int imageViewId) {
        ImageView imageView = (ImageView) activty.findViewById(imageViewId);
        into(imageView);
    }

    public void cancelLoad(ImageView imageView) {
        if(loadMap.containsKey(imageView)) {
            loadMap.get(imageView).cancelRequest();
        }
    }

    /**
     * The ImageView to load the image into
     *
     * @param imageView The ImageView to use
     */
    public void into(ImageView imageView) {
        if(imageView == null) {
            scrapBuilder();
            return;
        }

        ImageLoad imageLoad;

        //Stop old request if needed
        if(loadMap.containsKey(imageView)) {
            ImageLoad recycledLoad = loadMap.get(imageView);
            recycledLoad.cancelRequest();
            loadMap.remove(imageView);
            imageLoad = builder.build(recycledLoad, imageView);
        }
        else {
            imageLoad = builder.build(imageView);
        }

        loadMap.put(imageView, imageLoad);
        imageLoad.load();
        scrapBuilder();
    }

    /**
     * Cancels the request for the ImageView
     */
    public void cancel(ImageView imageView) {
        if(loadMap.containsKey(imageView)) {
            loadMap.get(imageView).cancelRequest();
            loadMap.remove(imageView);
        }
    }

    private void scrapBuilder() {
        builderScrap.push(builder);
    }

    public interface Listener {
        public void onLoad(boolean success, Bitmap bitmap, ImageView imageView);
    }
}
