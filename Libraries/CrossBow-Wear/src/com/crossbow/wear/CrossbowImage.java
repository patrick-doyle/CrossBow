package com.crossbow.wear;

import android.content.Context;

import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.toolbox.Crossbow;
import com.crossbow.volley.toolbox.FileImageLoader;

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
     * this will also create an new internal CrossbowImage to to store the load params
     *
     * @param context An application context
     * @return the current instance of the CrossbowImage
     */
    public static com.crossbow.volley.CrossbowImage.Builder from(Context context) {
        if(crossbowImage == null) {
            crossbowImage = new CrossbowImage(context.getApplicationContext());
        }
        return crossbowImage.newLoad();
    }

    private ImageLoader imageLoader;
    private FileImageLoader fileImageLoader;

    private CrossbowImage(Context context) {
        Crossbow crossbow = CrossbowWear.get(context);
        imageLoader = crossbow.getImageLoader();
        fileImageLoader = crossbow.getFileImageLoader();
    }

    private com.crossbow.volley.CrossbowImage.Builder newLoad() {
        return new com.crossbow.volley.CrossbowImage.Builder(imageLoader, fileImageLoader);
    }
}
