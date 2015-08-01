package com.crossbow.volley.toolbox;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;

/**
 * Created by Patrick on 27/07/2015.
 */
public abstract class GsonGetRequest<T> extends GsonRequest<T> {

    public GsonGetRequest(String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);
    }
}
