/**
 * Copyright (C) 14/01/2015 Patrick
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

package com.crossbow.volley.toolbox;

import android.graphics.Bitmap;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;

public class RecycleImageRequest extends ImageRequest {

    private final CrossbowImageCache crossbowImageCache;
    private final int mMaxWidth;
    private final int mMaxHeight;
    private final Bitmap.Config mDecodeConfig;
    private final static Object decodeLock = new Object();

    public RecycleImageRequest(CrossbowImageCache crossbowImageCache, String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
        super(url, listener, maxWidth, maxHeight, decodeConfig, errorListener);
        this.crossbowImageCache = crossbowImageCache;
        this.mMaxWidth = maxWidth;
        this.mMaxHeight = maxHeight;
        this.mDecodeConfig = decodeConfig;
    }

    @Override
    protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
        synchronized (decodeLock) {
            try {
                if (isCanceled()) {
                    return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                }
                Bitmap parsed = ImageDecoder.parseImage(response.data, mDecodeConfig, crossbowImageCache, mMaxWidth, mMaxHeight);
                if (parsed != null) {
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                }
                else {
                    return Response.error(new ParseError());
                }

            }
            catch (OutOfMemoryError | ParseError e) {
                VolleyLog.e("Caught OOM for %d byte image, url=%s", response.data.length, getUrl());
                return Response.error(new ParseError(e));
            }
        }
    }
}
