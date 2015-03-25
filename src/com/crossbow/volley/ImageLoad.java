package com.crossbow.volley;

import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.toolbox.CrossbowImage;

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
public class ImageLoad implements ViewTreeObserver.OnPreDrawListener, ImageLoader.ImageListener {

    private Props props;

    private ImageLoader.ImageContainer imageContainer;

    public void cancelRequest() {
        if(imageContainer != null) {
            imageContainer.cancelRequest();
        }
    }

    public void load() {
        ImageView imageView = props.imageView.get();
        if(imageView != null) {
            imageView.getViewTreeObserver().addOnPreDrawListener(this);
        }
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

        ImageView imageView = props.imageView.get();
        if(response.getBitmap() != null && imageView != null) {
            imageView.setImageBitmap(response.getBitmap());

            if(props.scaleType != null) {
                imageView.setScaleType(props.scaleType);
            }

            if(props.fade != 0 && !isImmediate) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
                alphaAnimation.setDuration(props.fade);
                imageView.startAnimation(alphaAnimation);
            }

            if(props.listener != null) {
                props.listener.onLoad(true, response.getBitmap(), imageView);
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        ImageView imageView = props.imageView.get();
        if(imageView != null && props.errorRes != 0) {
            imageView.setImageResource(props.errorRes);
        }
        else if (imageView != null) {
            imageView.setImageBitmap(null);
        }


        if(props.listener != null) {
            props.listener.onLoad(false, null, imageView);
        }
    }

    @Override
    public boolean onPreDraw() {

        ImageView view = props.imageView.get();
        if (view == null) {
            return true;
        }
        view.getViewTreeObserver().removeOnPreDrawListener(this);

        if (TextUtils.isEmpty(props.url)) {
            onErrorResponse(new VolleyError("Url Is empty"));
            return true;
        }

        if (!props.dontClear) {
            view.setImageBitmap(null);
        }

        if (props.defaultRes != 0) {
            view.setImageResource(props.defaultRes);
        }

        if (props.preScaleType != null) {
            view.setScaleType(props.preScaleType);
        }

        if (props.dontScale) {
            imageContainer = props.imageLoader.get(props.url, this);
        }
        else {
            imageContainer = props.imageLoader.get(props.url, this, view.getWidth(), view.getHeight());
        }
        return true;
    }

    /**
     * Internal reusable model used so properties are defined in one place.
     */

    private static class Props {

        private ImageLoader imageLoader;

        private int fade = 0;

        private String url = null;

        private int defaultRes = 0;

        private int errorRes = 0;

        private boolean dontClear;

        private ImageView.ScaleType scaleType;

        private ImageView.ScaleType preScaleType;

        private boolean dontScale;

        private WeakReference<ImageView> imageView;

        private CrossbowImage.Listener listener;
    }

    /**
     * Builder used for the Imageload. This will be useful for when we have different load types when the file loaders are added.
     */
    public static class Builder {

        private Props props;

        public Builder(ImageLoader imageLoader) {
            this();
            reset(imageLoader);
        }

        private Builder() {}

        public void reset(ImageLoader imageLoader) {
            props = new Props();
            props.imageLoader = imageLoader;
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

        public void dontClear(boolean dontClear) {
            props.dontClear = dontClear;
        }

        public void dontScale(boolean dontScale) {
            props.dontScale = dontScale;
        }

        public void url(String url) {
            props.url = url;
        }

        public ImageLoad build(ImageLoad imageLoad, ImageView imageView) {
            props.imageView = new WeakReference<ImageView>(imageView);
            imageLoad.props = props;
            return imageLoad;
        }

        public ImageLoad build(ImageView imageView) {
            return build(new ImageLoad(), imageView);
        }
    }
}
