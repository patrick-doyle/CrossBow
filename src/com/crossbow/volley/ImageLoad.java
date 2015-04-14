package com.crossbow.volley;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.toolbox.CrossbowImage;
import com.crossbow.volley.toolbox.FileImageLoader;
import com.crossbow.volley.toolbox.ScaleTypeDrawable;

import java.lang.ref.WeakReference;

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
public class ImageLoad implements ViewTreeObserver.OnPreDrawListener, ImageLoader.ImageListener, FileImageLoader.Listener {

    private Props props;

    private PendingImage pendingImage;

    private ImageLoader imageLoader;
    private FileImageLoader fileImageLoader;

    public ImageLoad(ImageLoader imageLoader, FileImageLoader fileImageLoader) {
        this.imageLoader = imageLoader;
        this.fileImageLoader = fileImageLoader;
    }

    public void cancelRequest() {
        if (pendingImage != null) {
            pendingImage.cancel();
        }
    }

    public void load() {
        ImageView imageView = props.imageView.get();
        if (imageView != null) {
            imageView.getViewTreeObserver().addOnPreDrawListener(this);
        }
    }

    @Override
    public boolean onPreDraw() {

        ImageView view = props.imageView.get();
        if (view == null) {
            return true;
        }
        view.getViewTreeObserver().removeOnPreDrawListener(this);

        if (!props.hasSource()) {
            setError(new VolleyError("No image source Is empty"));
            return true;
        }

        if (!props.dontClear) {
            view.setImageBitmap(null);
        }

        if (props.defaultRes != Props.DEFAULT) {
            Drawable drawable = view.getResources().getDrawable(props.defaultRes);
            ScaleTypeDrawable defaultScale = new ScaleTypeDrawable(drawable, props.preScaleType);
            view.setImageDrawable(defaultScale);
        }

        int width = 0;
        int height = 0;
        if (!props.dontScale) {
            width = view.getWidth();
            height = view.getHeight();

            if (width < 1000 && height < 1000) {
                //round to the nearest 100
                width = ((width + 99) / 100) * 100;
                height = ((height + 99) / 100) * 100;
            } else {
                //round to the nearest 10
                width = ((width + 9) / 10) * 10;
                height = ((height + 9) / 10) * 10;
            }
        }
        loadImage(view, props.drawable, props.file, props.url, width, height);

        return true;
    }

    private void loadImage(ImageView imageView, int drawable, String file, String url, int width, int height) {
        if(pendingImage != null) {
            pendingImage.cancel();
        }

        pendingImage = new PendingImage();

        if(url != null && file == null && drawable == Props.DEFAULT) {
            pendingImage.imageContainer = imageLoader.get(url, this, width, height);
        }
        else if(url == null && file != null && drawable == Props.DEFAULT) {
            pendingImage.fileImageContainer = fileImageLoader.get(file, width, height, this);
        }
        else if(url == null && file == null && drawable != Props.DEFAULT) {
            imageView.setImageResource(drawable);
        }
        else {
            onErrorResponse(new VolleyError("No image source defined"));
        }
    }

    private void setBitmap(Bitmap bitmap, boolean fromCache) {
        ImageView imageView = props.imageView.get();
        if (bitmap != null && imageView != null) {

            if (props.fade != Props.DEFAULT && !fromCache) {

                //Do a fade with an animation drawable
                Resources resources = imageView.getResources();

                if (props.defaultRes != Props.DEFAULT) {

                    BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
                    Drawable inital = resources.getDrawable(props.defaultRes);
                    ScaleTypeDrawable defaultScale = new ScaleTypeDrawable(inital, props.preScaleType);
                    ScaleTypeDrawable bitmapScale = new ScaleTypeDrawable(bitmapDrawable, props.scaleType);

                    Drawable[] layers = new Drawable[2];
                    layers[0] = defaultScale;
                    layers[1] = bitmapScale;

                    TransitionDrawable animationDrawable = new TransitionDrawable(layers);
                    imageView.setImageDrawable(animationDrawable);
                    animationDrawable.startTransition(props.fade);
                }
                else {
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
                    alphaAnimation.setDuration(props.fade);
                    imageView.setImageBitmap(bitmap);
                    imageView.startAnimation(alphaAnimation);
                }
            }
            else {
                //just set the bitmap
                imageView.setImageBitmap(bitmap);
            }

            if (props.listener != null) {
                props.listener.onLoad(true, bitmap, imageView);
            }
        }
    }

    private void setError(VolleyError volleyError) {
        ImageView imageView = props.imageView.get();
        if (imageView != null && props.errorRes != Props.DEFAULT) {

            Drawable drawable = imageView.getResources().getDrawable(props.defaultRes);
            ScaleTypeDrawable errorScaleDrawable = new ScaleTypeDrawable(drawable, props.errorScaleType);

            if(props.defaultRes != 0 && props.fade != Props.DEFAULT) {
                //have a default and a fade, do a cross fade;
                Drawable defaultDrawable = imageView.getResources().getDrawable(props.defaultRes);
                ScaleTypeDrawable defaultScale = new ScaleTypeDrawable(defaultDrawable, props.preScaleType);

                Drawable[] layers = new Drawable[2];
                layers[0] = defaultScale;
                layers[1] = errorScaleDrawable;

                TransitionDrawable animationDrawable = new TransitionDrawable(layers);
                imageView.setImageDrawable(animationDrawable);
                animationDrawable.startTransition(props.fade);
            }
            else {
                imageView.setImageDrawable(errorScaleDrawable);
            }
        }
        else if (imageView != null) {
            imageView.setImageBitmap(null);
        }

        if (props.listener != null) {
            props.listener.onLoad(false, null, imageView);
        }
    }

    /**
     * called when the network image loader returns
     */
    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

        ImageView imageView = props.imageView.get();
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


    /**
     * Internal reusable model used so properties are defined in one place.
     */

    private static class Props {

        private static final int DEFAULT = -1;

        private int fade = DEFAULT;

        private String url = null;

        private String file = null;

        private int drawable = DEFAULT;

        private int defaultRes = DEFAULT;

        private int errorRes = DEFAULT;

        private boolean dontClear = false;

        private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;

        private ImageView.ScaleType errorScaleType = ImageView.ScaleType.CENTER;

        private ImageView.ScaleType preScaleType = ImageView.ScaleType.CENTER;

        private boolean dontScale = false;

        private WeakReference<ImageView> imageView;

        private CrossbowImage.Listener listener;

        private boolean hasSource() {
            return !TextUtils.isEmpty(url) || !TextUtils.isEmpty(file) || drawable != DEFAULT;
        }
    }

    /**
     * Builder used for the Imageload. This will be useful for when we have different load types when the file loaders are added.
     */
    public static class Builder {

        private Props props;

        private ImageLoader imageLoader;

        private FileImageLoader fileImageLoader;

        public Builder(ImageLoader imageLoader, FileImageLoader fileImageLoader) {
            reset(imageLoader, fileImageLoader);
        }

        public void reset(ImageLoader imageLoader, FileImageLoader fileImageLoader) {
            this.imageLoader = imageLoader;
            this.fileImageLoader = fileImageLoader;
            props = new Props();
        }

        public void listen(CrossbowImage.Listener listener) {
            props.listener = listener;
        }

        public void fade(int fade) {
            props.fade = fade;
        }

        public void defaultRes(@DrawableRes int defaultRes) {
            props.defaultRes = defaultRes;
        }

        public void errorRes(@DrawableRes int errorRes) {
            props.errorRes = errorRes;
        }

        public void scaleType(ImageView.ScaleType scaleType) {
            props.scaleType = scaleType;
        }

        public void preScaleType(ImageView.ScaleType preScaleType) {
            props.preScaleType = preScaleType;
        }

        public void errorScaleType(ImageView.ScaleType errorScaleType) {
            props.errorScaleType = errorScaleType;
        }

        public void dontClear(boolean dontClear) {
            props.dontClear = dontClear;
        }

        public void dontScale(boolean dontScale) {
            props.dontScale = dontScale;
        }

        public void url(String url) {
            props.url = url;
        }

        public void file(String file) {
            props.file = file;
        }

        public void drawable(int drawable) {
            props.drawable = drawable;
        }

        public ImageLoad build(ImageLoad imageLoad, ImageView imageView) {
            props.imageView = new WeakReference<ImageView>(imageView);
            imageLoad.props = props;
            return imageLoad;
        }

        public ImageLoad build(ImageView imageView) {
            return build(new ImageLoad(imageLoader, fileImageLoader), imageView);
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
