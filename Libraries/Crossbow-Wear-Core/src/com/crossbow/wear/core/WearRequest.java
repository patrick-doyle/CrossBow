package com.crossbow.wear.core;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Interface to be implemented by requests that need their responses parsed/shrunk on the handheld device
 * before being sent to wearable.
 */
public interface WearRequest {

    /**
     * Bundle to be passed into the {@link com.crossbow.wear.core.ResponseTransformer#transform(Bundle, byte[])}  ResponseTransformer}
     * method to help with shrinking requests
     *
     * @return a bundle of args
     */
    public @Nullable ParamsBundle getTransformerParams();

    /**
     * The key used to map the {@link com.crossbow.wear.core.ResponseTransformer ResponseTransformer}
     * to the request on the phone. The {@link com.crossbow.wear.core.ResponseTransformer ResponseTransformer}
     * is registered on the phone via overriding the {@link com.crossbow.wear.receiver.CrossbowListenerService#onGetTransformerMap() CrossbowListenerService}
     * method.
     *
     * @return the string key to use
     */
    public @Nullable String getTransFormerKey();
}
