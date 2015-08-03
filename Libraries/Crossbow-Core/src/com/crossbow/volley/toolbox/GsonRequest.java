package com.crossbow.volley.toolbox;

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
 * Base class for preforming request that parse data using gson. Needs to be subclassed to prevent
 * java type erasure
 * <code>
 *    <pre>
 *        GsonRequest request = new GsonRequest(params...){}; <-- important curly braces
 *    </pre>
 * </code>
 */
public abstract class GsonRequest<T> extends Request<T> {

    private final Response.Listener<T> listener;
    private static final Gson gson = new Gson();
    private final Type type;

    public GsonRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.type = new TypeToken<T>(){}.getType();
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
