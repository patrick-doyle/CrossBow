package com.crossbow.wear;

import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crossbow.volley.toolbox.NetworkImageLoader;
import com.crossbow.wear.toolbox.WearImageRequest;

/**

 */
public class WearImageLoader extends NetworkImageLoader {
    /**
     * Constructs a new ImageLoader that can uses the Image decoder.
     *
     * @param queue      The RequestQueue to use for making image requests.
     * @param imageCache The cache to use as an L1 cache.
     */
    public WearImageLoader(RequestQueue queue, ImageCache imageCache) {
        super(queue, imageCache);
    }

    @Override
    protected Request<Bitmap> makeImageRequest(String requestUrl, int maxWidth, int maxHeight, final String cacheKey) {
        return new WearImageRequest(requestUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                onGetImageSuccess(cacheKey, response);
            }
        }, maxWidth, maxHeight, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onGetImageError(cacheKey, error);
            }
        });
    }
}
