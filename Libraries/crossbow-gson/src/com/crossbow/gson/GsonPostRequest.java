package com.crossbow.gson;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Wrapper around the {@link GsonRequest} for post requests. Needs to be subclassed to prevent
 * java type erasure
 * <code>
 *    <pre>
 *        GsonRequest request = new GsonRequest(params...){}; <-- important curly braces
 *    </pre>
 * </code>
 */
public abstract class GsonPostRequest<T> extends GsonRequest<T> {

    private final Object postBody;

    public GsonPostRequest(Object postBody, Gson gson, Type type, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, gson, type, listener, errorListener);
        this.postBody = postBody;
    }

    public GsonPostRequest(Object postBody, String url, Type type, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(postBody, DEFAULT_GSON, type, url, listener, errorListener);
    }

    @Deprecated
    public GsonPostRequest(Object postBody, Gson gson, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, gson, listener, errorListener);
        this.postBody = postBody;
    }

    @Deprecated
    public GsonPostRequest(Object postBody, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(postBody, DEFAULT_GSON, url, listener, errorListener);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return getGson().toJson(postBody).getBytes();
    }
}
