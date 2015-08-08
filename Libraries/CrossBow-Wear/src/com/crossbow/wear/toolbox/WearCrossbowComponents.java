package com.crossbow.wear.toolbox;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.crossbow.volley.toolbox.DefaultCrossbowComponents;
import com.crossbow.wear.PlayNetwork;
import com.squareup.okhttp.OkHttpClient;

/**
 * Used to get the correct version of WearCrossbowComponents that uses the {@link PlayNetwork} to transmit
 * data to the handheld. If you are writing a custom stack please use the {@link PlayNetwork} as
 * the network or request will not be sent to wearable.
 */
public class WearCrossbowComponents extends DefaultCrossbowComponents {

    public WearCrossbowComponents(Context context) {
        super(context);
    }

    @Override
    public OkHttpClient onCreateHttpClient() {
        return null;
    }

    @Override
    public HttpStack onCreateHttpStack(OkHttpClient okHttpClient) {
        return null;
    }

    @Override
    public Network onCreateNetwork(HttpStack httpStack) {
        return new PlayNetwork(getContext());
    }

    @Override
    public RequestQueue onCreateRequestQueue(Cache cache, Network network) {
        RequestQueue requestQueue = new RequestQueue(cache, network, 3);
        requestQueue.start();
        return requestQueue;
    }
}
