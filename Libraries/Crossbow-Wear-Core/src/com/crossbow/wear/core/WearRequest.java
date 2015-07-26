package com.crossbow.wear.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.android.gms.wearable.DataMap;

/**
 * Created by Patrick on 10/07/2015.
 */
public interface WearRequest {

    public @Nullable Bundle getTransformerParams();

    public @Nullable String getTransFormerKey();
}
