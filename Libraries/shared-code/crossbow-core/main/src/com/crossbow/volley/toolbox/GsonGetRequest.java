package com.crossbow.volley.toolbox;

import com.android.volley.Response;

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

    public GsonGetRequest(String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);
    }
}
