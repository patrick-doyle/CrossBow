package com.crossbow.wear.toolbox;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.crossbow.wear.core.WearFlags;
import com.crossbow.volley.toolbox.RecycleImageRequest;
import com.crossbow.wear.core.WearRequest;

/**
 * Created by Patrick on 10/07/2015.
 */
public class WearImageRequest extends RecycleImageRequest implements WearRequest {

    private final int maxWidth;
    private final int maxHeight;

    public WearImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
        super(url, listener, maxWidth, maxHeight, decodeConfig, errorListener);
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    @NonNull
    @Override
    public Bundle getTransformerParams() {
        Bundle bundle = new Bundle();
        bundle.putInt("maxWidth", maxWidth);
        bundle.putInt("maxHeight", maxHeight);
        return bundle;
    }

    @Override
    public String getTransFormerKey() {
        return WearFlags.IMAGE_TRANSFORMER_KEY;
    }
}
