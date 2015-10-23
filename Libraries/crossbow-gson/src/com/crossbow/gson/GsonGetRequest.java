package com.crossbow.gson;

import com.android.volley.Response;
import com.google.gson.Gson;

/**
 * Wrapper around the {@link GsonRequest} for get requests. Needs to be subclassed to prevent
 * java type erasure
 * <code>
 *    <pre>
 *        GsonRequest request = new GsonRequest(params...){}; <-- important curly braces
 *    </pre>
 * </code>
 */
public abstract class GsonGetRequest<T> extends GsonRequest<T> {

    public GsonGetRequest(String url, Gson gson, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, gson, listener, errorListener);
    }

    public GsonGetRequest(String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(url, DEFAULT_GSON, listener, errorListener);    }
}
