package com.crossbow.volley.toolbox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.ImageProperties;

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
    private Listener listener;

    /**
     * Get an instance of TwistImage
     *
     * @param context
     * @return
     */
    public static CrossbowImage from(Context context) {
        if(crossbowImage == null) {
            crossbowImage = new CrossbowImage(context);
        }
        return crossbowImage;
    }

    private ImageLoader imageLoader;
    private String url;
    private int defaultRes;
    private int errorRes;
    private int fade;
    private boolean dontClear;
    private boolean dontScale;
    private ImageView.ScaleType scaleType;
    private ImageView.ScaleType preScaleType;
    private Crossbow crossbow;
    private WeakHashMap<ImageView, ImageProperties> propertiesMap = new WeakHashMap<ImageView, ImageProperties>();

    private CrossbowImage(Context context) {
        crossbow = Crossbow.get(context);
        imageLoader = crossbow.getImageLoader();
    }

    /**
     * Sets the url to load
     *
     * @param url the url for the image
     */
    public CrossbowImage url(String url) {
        this.url = url;
        return this;
    }

    /**
     * Sets the fade in duration for when the image loads.
     *
     * @param duration the duration to fade
     */
    public CrossbowImage fade(int duration) {
        this.fade = duration;
        return this;
    }

    /**
     * The drawable res to use when the image fails to load
     *
     * @param errorRes the drawable res to use
     */
    public CrossbowImage error(@DrawableRes int errorRes) {
        this.errorRes = errorRes;
        return this;
    }

    /**
     * The drawable res to show while the image loads.
     *
     * @param defaultRes the drawable res to use
     */
    public CrossbowImage defaultRes(@DrawableRes int defaultRes) {
        this.defaultRes = defaultRes;
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
    public CrossbowImage defaultScale(ImageView.ScaleType scaleType) {
        this.preScaleType = scaleType;
        return this;
    }

    /**
     * Prevent the Imagefrom getting scaled at all. <b>Will cause the image to load the full size image into the cache</b>
     *
     */
    public CrossbowImage dontScale() {
        this.dontScale = true;
        return this;
    }

    /**
     * Sets the image scaletype to fit center when the image loads
     */
    public CrossbowImage fit() {
        return scale(ImageView.ScaleType.FIT_CENTER);
    }

    /**
     * Sets the image scaletype to fit center when the image loads
     */
    public CrossbowImage listen(Listener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Sets the image scaletype to this when the image loads
     *
     * @param scaleType th scaletype to set.
     */
    public CrossbowImage scale(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }

    public CrossbowImage dontClear() {
        this.dontClear = true;
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
        if(propertiesMap.containsKey(imageView)) {
            propertiesMap.get(imageView).cancelRequest();
        }
    }

    /**
     * The ImageView to load the image into
     *
     * @param imageView The ImageView to use
     */
    public void into(ImageView imageView) {
        if(imageView == null) {
            cleanUpConfig();
            return;
        }

        //Stop old request if needed and reuse the props

        ImageProperties imageProperties;

        if(propertiesMap.containsKey(imageView)) {
            propertiesMap.get(imageView).cancelRequest();
            imageProperties = propertiesMap.get(imageView);
            imageProperties.clean();
        }
        else {
            imageProperties = new ImageProperties();
        }

        imageProperties.url = url;
        imageProperties.fade = fade;
        imageProperties.defaultRes = defaultRes;
        imageProperties.errorRes = errorRes;
        imageProperties.scaleType = scaleType;
        imageProperties.dontClear = dontClear;
        imageProperties.preScaleType = preScaleType;
        imageProperties.dontScale = dontScale;
        imageProperties.listener = listener;
        imageProperties.setImageLoader(imageLoader);
        imageProperties.setImageView(imageView);

        imageView.getViewTreeObserver().addOnPreDrawListener(imageProperties);
        propertiesMap.put(imageView, imageProperties);
        cleanUpConfig();
    }

    /**
     * Cancels the request for the ImageView
     */
    public void cancel(ImageView imageView) {
        if(propertiesMap.containsKey(imageView)) {
            propertiesMap.get(imageView).cancelRequest();
            propertiesMap.remove(imageView);
        }
    }

    private void cleanUpConfig() {
        fade = 0;
        defaultRes = 0;
        errorRes = 0;
        url = null;
        scaleType = null;
        preScaleType = null;
        dontClear = false;
        dontScale = false;
        listener = null;
    }

    public interface Listener {
        public void onLoad(boolean success, Bitmap bitmap, ImageView imageView);
    }
}
