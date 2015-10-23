package com.crossbow.gson;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Deprecated will be moved to its own library
 * <code>
 *    <pre>
 *        GsonRequest request = new GsonRequest(params...){}; <-- important curly braces
 *    </pre>
 * </code>
 */
public abstract class GsonRequest<T> extends Request<T> {

    protected static final Gson DEFAULT_GSON = new Gson();

    private final Response.Listener<T> listener;
    private final Gson gson;
    private final Type type;

    public GsonRequest(int method, String url, Gson gson, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.type = new TypeToken<T>(){}.getType();
        this.gson = gson;
    }

    public GsonRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(method, url, DEFAULT_GSON, listener, errorListener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(response.data);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));

        T parsedData = getGson().fromJson(jsonReader, type);
        return Response.success(parsedData, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    protected Gson getGson() {
        return gson;
    }
}
