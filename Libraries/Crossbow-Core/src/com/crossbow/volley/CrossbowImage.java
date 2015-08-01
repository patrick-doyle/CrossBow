package com.crossbow.volley;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.toolbox.FileImageLoader;

import java.io.File;
import java.lang.ref.WeakReference;
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
public class CrossbowImage implements ViewTreeObserver.OnPreDrawListener, ImageLoader.ImageListener, FileImageLoader.Listener {

    private static Rect dirtyRect = new Rect(0, 0, 1, 1);

    private static final int DEFAULT = -1;

    private int fade = DEFAULT;

    private String url = null;

    private String file = null;

    private int drawable = DEFAULT;

    private Drawable defaultDrawable = null;

    private Drawable errorDrawable = null;

    private boolean dontClear = false;

    private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;

    private ImageView.ScaleType errorScaleType = ImageView.ScaleType.CENTER;

    private ImageView.ScaleType preScaleType = ImageView.ScaleType.CENTER;

    private boolean dontScale = false;

    private boolean debug = false;

    private WeakReference<ImageView> imageView;

    private Listener listener;

    private PendingImage pendingImage;

    private ImageLoader imageLoader;

    private FileImageLoader fileImageLoader;

    private int width = DEFAULT;

    private int height = DEFAULT;

    private static WeakHashMap<ImageView, CrossbowImage> inProgressLoads = new WeakHashMap<>();

    private CrossbowImage(ImageLoader imageLoader, FileImageLoader fileImageLoader) {
        this.imageLoader = imageLoader;
        this.fileImageLoader = fileImageLoader;
    }

    public void cancelRequest() {
        if (pendingImage != null) {
            pendingImage.cancel();
        }
    }

    public void load() {
        ImageView imageView = this.imageView.get();
        if (imageView != null) {

            if(inProgressLoads.containsKey(imageView)) {
                inProgressLoads.get(imageView).cancelRequest();
                inProgressLoads.remove(imageView);
            }

            inProgressLoads.put(imageView, this);

            imageView.getViewTreeObserver().addOnPreDrawListener(this);
            imageView.invalidate(dirtyRect);
        }
    }

    public void cancel(ImageView imageView) {
        if(inProgressLoads.containsKey(imageView)) {
            inProgressLoads.get(imageView).cancelRequest();
            inProgressLoads.remove(imageView);
        }
    }

    @Override
    public boolean onPreDraw() {

        ImageView view = this.imageView.get();
        if (view == null) {
            return true;
        }
        view.getViewTreeObserver().removeOnPreDrawListener(this);

        if (!this.hasSource()) {
            setError(new VolleyError("No image source Is empty"));
            return true;
        }

        if (!this.dontClear) {
            view.setImageBitmap(null);
        }

        if (this.defaultDrawable != null) {
            ScaleTypeDrawable defaultScale = new ScaleTypeDrawable(defaultDrawable, this.preScaleType);
            view.setImageDrawable(defaultScale);
        }

        int width = 0;
        int height = 0;
        if(this.width != DEFAULT && this.height != DEFAULT) {
            //Use provided width and height if provided
            width = this.width;
            height = this.height;
        }
        else if (!this.dontScale) {
            //Use the raw image
            width = view.getWidth();
            height = view.getHeight();
        }
        loadImage(view, this.drawable, this.file, this.url, width, height);

        return true;
    }

    private void loadImage(ImageView imageView, int drawableID, String file, String url, int width, int height) {
        if(pendingImage != null) {
            pendingImage.cancel();
        }

        pendingImage = new PendingImage();

        if(url != null && file == null && drawableID == DEFAULT) {
            pendingImage.imageContainer = imageLoader.get(url, this, width, height);
        }
        else if(url == null && file != null && drawableID == DEFAULT) {
            pendingImage.fileImageContainer = fileImageLoader.get(file, width, height, this);
        }
        else if(url == null && file == null && drawableID != DEFAULT) {
            ScaleTypeDrawable defaultScale = new ScaleTypeDrawable(defaultDrawable, this.preScaleType);
            imageView.setImageDrawable(defaultScale);
        }
        else {
            onErrorResponse(new VolleyError("No image source defined"));
        }
    }

    @VisibleForTesting
    void setBitmap(Bitmap bitmap, boolean fromCache) {
        ImageView imageView = this.imageView.get();
        if (bitmap != null && imageView != null) {

            Resources resources = imageView.getResources();
            BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
            ScaleTypeDrawable bitmapScale = new ScaleTypeDrawable(bitmapDrawable, this.scaleType, this.debug && fromCache);

            if (this.fade != DEFAULT && !fromCache) {

                //Do a fade with an animation drawable

                if (defaultDrawable != null) {
                    ScaleTypeDrawable defaultScale = new ScaleTypeDrawable(defaultDrawable, this.preScaleType);

                    Drawable[] layers = new Drawable[2];
                    layers[0] = defaultScale;
                    layers[1] = bitmapScale;

                    TransitionDrawable animationDrawable = new TransitionDrawable(layers);
                    imageView.setImageDrawable(animationDrawable);
                    animationDrawable.setCrossFadeEnabled(true);
                    animationDrawable.startTransition(this.fade);
                }
                else {
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
                    alphaAnimation.setDuration(this.fade);
                    imageView.setImageDrawable(bitmapScale);
                    imageView.startAnimation(alphaAnimation);
                }
            }
            else {
                //just set the bitmap
                imageView.setImageDrawable(bitmapScale);
            }

            if (this.listener != null) {
                this.listener.onLoad(true, fromCache, bitmap, imageView);
            }
        }
    }

    @VisibleForTesting
    void setError(VolleyError volleyError) {
        ImageView imageView = this.imageView.get();
        if(imageView == null) {
            return;
        }
        if (errorDrawable != null) {
            ScaleTypeDrawable errorScaleDrawable = new ScaleTypeDrawable(errorDrawable, this.errorScaleType);

            if(this.defaultDrawable != null && this.fade != DEFAULT) {
                //have a default and a fade, do a cross fade;
                ScaleTypeDrawable defaultScale = new ScaleTypeDrawable(defaultDrawable, this.preScaleType);

                Drawable[] layers = new Drawable[2];
                layers[0] = defaultScale;
                layers[1] = errorScaleDrawable;

                TransitionDrawable animationDrawable = new TransitionDrawable(layers);
                animationDrawable.setCrossFadeEnabled(true);
                imageView.setImageDrawable(animationDrawable);
                animationDrawable.startTransition(this.fade);
            }
            else {
                imageView.setImageDrawable(errorScaleDrawable);
            }
        }
        else if (defaultDrawable != null) {
            ScaleTypeDrawable defaultScale = new ScaleTypeDrawable(defaultDrawable, this.preScaleType);
            imageView.setImageDrawable(defaultScale);
        }
        else {
            imageView.setImageBitmap(null);
        }

        if (this.listener != null) {
            this.listener.onLoad(false, false, null, imageView);
        }
    }

    /**
     * called when the network image loader returns
     */
    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

        ImageView imageView = this.imageView.get();
        if(imageView != null && response.getBitmap() != null) {
            setBitmap(response.getBitmap(), isImmediate);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        setError(error);
    }

    /**
     * Called when the file request returns
     */
    @Override
    public void onImageLoad(Bitmap bitmap, boolean fromCache) {
        setBitmap(bitmap, fromCache);
    }

    @Override
    public void onImageError(VolleyError error) {
        setError(error);
    }

    private boolean hasSource() {
        return !TextUtils.isEmpty(url) || !TextUtils.isEmpty(file) || drawable != DEFAULT;
    }

    public interface Listener {
        public void onLoad(boolean success, boolean fromCache, Bitmap bitmap, ImageView imageView);
    }

    /**
     * Builder used for the Imageload. This will be useful for when we have different load types when the file loaders are added.
     */
    public static class Builder {

        private final Context context;

        private CrossbowImage crossbowImage;

        private int defaultRes = DEFAULT;

        private int errorRes = DEFAULT;

        public Builder(Context context, ImageLoader imageLoader, FileImageLoader fileImageLoader) {
            this.context = context.getApplicationContext();
            crossbowImage = new CrossbowImage(imageLoader, fileImageLoader);
        }

        /**
         * Sets a listener to allow you to see when the image loads
         */
        public Builder listen(Listener listener) {
            this.crossbowImage.listener = listener;
            return this;
        }

        /**
         * Sets the fade in duration for when the image loads.
         *
         * @param duration the duration to fade
         */
        public Builder fade(int duration) {
            this.crossbowImage.fade = duration;
            return this;
        }

        /**
         * Sets a pixel size for the image to be decoded to ignoring the size of the imageview
         */
        public Builder pixelSize(int width, int height) {
            this.crossbowImage.width = width;
            this.crossbowImage.height = height;
            return this;
        }

        /**
         * Sets a density pixel size size for the image to be decoded to ignoring the size of the imageview
         */
        public Builder dpSize(int width, int height) {
            this.crossbowImage.width = width;
            this.crossbowImage.height = height;
            return this;
        }

        /**
         * The drawable res to use when the image is loading
         *
         * @param defaultRes the drawable res to use
         */
        public Builder placeholder(@DrawableRes int defaultRes) {
            this.crossbowImage.defaultDrawable = context.getResources().getDrawable(defaultRes);
            return this;
        }

        /**
         * The drawable res to use when the image fails to load
         *
         * @param errorRes the drawable res to use
         */
        public Builder error(@DrawableRes int errorRes) {
            this.crossbowImage.errorDrawable = context.getResources().getDrawable(errorRes);
            return this;
        }

        /**
         * The drawable to use when the image is loading
         *
         * @param defaultDrawable the drawable res to use
         */
        public Builder placeholder(Drawable defaultDrawable) {
            this.crossbowImage.defaultDrawable = defaultDrawable;
            return this;
        }

        /**
         * The drawable to use when the image fails to load
         *
         * @param error the drawable res to use
         */
        public Builder error(Drawable error) {
            this.crossbowImage.errorDrawable = error;
            return this;
        }

        /**
         * Sets the image {@link ImageView.ScaleType Scaletype} to center crop when the image loads
         */
        public Builder centerCrop() {
            return scale(ImageView.ScaleType.CENTER_CROP);
        }

        /**
         * Sets the image {@link ImageView.ScaleType Scaletype} to use when the image loads
         */
        public Builder scale(ImageView.ScaleType scaleType) {
            this.crossbowImage.scaleType = scaleType;
            return this;
        }

        /**
         * Sets the scale type for the default/placeholder image.
         *
         * @param preScaleType {@link ImageView.ScaleType Scaletype} to set.
         */
        public Builder placeholderScale(ImageView.ScaleType preScaleType) {
            this.crossbowImage.preScaleType = preScaleType;
            return this;
        }

        /**
         * Sets the scale type for the error image image.
         *
         * @param errorScaleType {@link ImageView.ScaleType Scaletype} to set.
         */
        public Builder errorScale(ImageView.ScaleType errorScaleType) {
            this.crossbowImage.errorScaleType = errorScaleType;
            return this;
        }

        /**
         * Dont remove the old image before setting the new image
         */
        public Builder dontClear() {
            this.crossbowImage.dontClear = true;
            return this;
        }
        /**
         * This will draw an extra green marker on the image if it was from the memory cache
         */
        public Builder debug() {
            this.crossbowImage.debug = true;
            return this;
        }

        /**
         * Loads the raw bitmap <b>Will cause the image to load the raw FULL SIZE image</b>
         */
        public Builder dontScale() {
            this.crossbowImage.dontScale = true;
            return this;
        }

        /**
         * Sets the image scaletype to fit center when the image loads
         */
        public Builder fitInto() {
            return scale(ImageView.ScaleType.FIT_CENTER);
        }

        /**
         * @see #into(View, int)
         * @param root root view to look in
         * @param imageViewId id of the imageview
         */
        public Builder into(View root, @IdRes int imageViewId) {
            ImageView imageView = (ImageView) root.findViewById(imageViewId);
            return into(imageView);
        }

        /**
         * @see #into(View, int)
         */
        public Builder into(Activity activty, @IdRes int imageViewId) {
            ImageView imageView = (ImageView) activty.findViewById(imageViewId);
            return into(imageView);
        }

        /**
         * Sets the string source to load
         *
         * @param source the source for the image (file path or http/https url)
         */
        public Builder source(String source) {
            if(URLUtil.isNetworkUrl(source)) {
                crossbowImage.url = source;
            }
            else {
                crossbowImage.file = source;
            }
            return this;
        }

        /**
         * Sets the file uri source to load
         *
         * @param source the source for the image (File Uri)
         */
        public Builder source(Uri source) {
            crossbowImage.file = new File(source.getPath()).getAbsolutePath();
            return this;
        }

        /**
         * Sets the drawable resource to load (This runs on the main thread)
         *
         * @param drawable the resource id of thr drawable to load
         */
        public Builder source(int drawable) {
            this.crossbowImage.drawable = drawable;
            return this;
        }

        /**
         * Starts the image load
         */
        public void load() {
            if(crossbowImage.imageView == null) {
                throw new IllegalArgumentException("Image view must not by null");
            }
            crossbowImage.load();
        }

        public Builder into(@NonNull ImageView imageView) {
            this.crossbowImage.imageView = new WeakReference<ImageView>(imageView);
            return this;
        }
    }

    private static class PendingImage {
        private ImageLoader.ImageContainer imageContainer;

        private FileImageLoader.FileImageContainer fileImageContainer;

        public void cancel() {
            if(imageContainer != null) {
                imageContainer.cancelRequest();
            }
            else if(fileImageContainer != null) {
                fileImageContainer.cancel();
            }
        }
    }
}
