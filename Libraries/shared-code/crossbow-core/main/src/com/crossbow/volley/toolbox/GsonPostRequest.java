package com.crossbow.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.google.gson.Gson;

/**
 * Wrapper around the {@link GsonRequest} for post requests. Needs to be subclassed to prevent
 * java type erasure
 * <code>
 *    <pre>
 *        GsonRequest request = new GsonRequest(params...){}; <-- important curly braces
 *    </pre>
 * </code>
 */
@Deprecated
public abstract class GsonPostRequest<T> extends GsonRequest<T> {

    private final Object postBody;

    public GsonPostRequest(Object postBody, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);
        this.postBody = postBody;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return getGson().toJson(postBody).getBytes();
    }
}
