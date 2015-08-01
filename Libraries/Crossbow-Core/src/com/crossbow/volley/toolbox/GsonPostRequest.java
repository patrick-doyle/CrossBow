package com.crossbow.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.google.gson.Gson;

/**
 * Created by Patrick on 27/07/2015.
 */
public abstract class GsonPostRequest<T> extends GsonRequest<T> {

    private final Object postBody;
    private static final Gson gson = new Gson();

    public GsonPostRequest(Object postBody, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);
        this.postBody = postBody;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return getGson().toJson(postBody).getBytes();
    }
}
