package com.crossbow.wear.toolbox;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.Response;
import com.crossbow.volley.toolbox.RecycleImageRequest;
import com.crossbow.wear.core.ParamsBundle;
import com.crossbow.wear.core.WearConstants;
import com.crossbow.wear.core.WearRequest;

/**
 * Image request to be used for requesting images for the wearable. It correctly handles setting up the
 * transformer on the phone to reduce the images to a manageable size for the wearable
 */
public class WearImageRequest extends RecycleImageRequest implements WearRequest {

    private final int maxWidth;
    private final int maxHeight;
    private final Bitmap.Config config;

    public WearImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, Bitmap.Config decodeConfig,
                            ImageView.ScaleType scaleType, Response.ErrorListener errorListener) {
        super(url, listener, maxWidth, maxHeight, decodeConfig, scaleType, errorListener);
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        config = decodeConfig;
    }

    @Override
    public ParamsBundle getTransformerParams() {
        ParamsBundle bundle = new ParamsBundle();
        bundle.putInt("maxWidth", maxWidth);
        bundle.putInt("maxHeight", maxHeight);
        bundle.putSerializable("config", config);
        return bundle;
    }

    @Override
    public String getTransFormerKey() {
        return WearConstants.IMAGE_TRANSFORMER_KEY;
    }
}
