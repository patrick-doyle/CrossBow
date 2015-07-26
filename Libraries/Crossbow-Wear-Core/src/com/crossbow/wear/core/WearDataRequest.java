package com.crossbow.wear.core;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.gms.wearable.DataMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Patrick on 11/07/2015.
 */
public class WearDataRequest extends Request<NetworkResponse> {

    private Listener listener;

    private ErrorListener errorListener;

    private final String uuid;

    private final String transformerKey;

    private final Map<String, ResponseTransformer> transformerMap = new HashMap<>(0);

    private final String cacheKey;

    private final String tag;

    private final String bodyContentType;

    private final Map<String, String> headers;

    private final byte[] body;

    private final Priority priority;

    private final Bundle transformerArgs;

    private final RetryPolicy retryPolicy;

    public WearDataRequest(int method, String url, String uuid, String transformerKey, int retryCount, int timeout, String cacheKey, String tag, String bodyContentType, Map<String, String> headers, byte[] body, Priority priority, Bundle transformerArgs) {
        super(method, url, null);
        this.uuid = uuid;
        this.cacheKey = cacheKey;
        this.tag = tag;
        this.bodyContentType = bodyContentType;
        this.headers = headers;
        this.body = body;
        this.priority = priority;
        this.transformerArgs = transformerArgs;
        this.transformerMap.putAll(transformerMap);
        this.retryPolicy = new DefaultRetryPolicy(timeout, retryCount, 1f);
        this.transformerKey = transformerKey;
        setRetryPolicy(retryPolicy);
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        if(transformerMap.containsKey(transformerKey)) {
            try {
                byte[] data = transformerMap.get(transformerKey).transform(transformerArgs, response.data);
                return Response.success(new NetworkResponse(response.statusCode, data, response.headers, response.notModified),
                        HttpHeaderParser.parseCacheHeaders(response));
            }
            catch (ParseError parseError) {
                return Response.error(parseError);
            }
        }
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        listener.onResponse(uuid, response);
    }

    @Override
    public void deliverError(VolleyError error) {
        errorListener.onErrorResponse(uuid, error);
    }

    @Override
    public String getCacheKey() {
        return cacheKey;
    }

    @Override
    public Object getTag() {
        return tag;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return body;
    }

    @Override
    public String getBodyContentType() {
        return bodyContentType;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers != null ? headers : Collections.<String, String>emptyMap();
    }

    public String getUuid() {
        return uuid;
    }

    public Bundle getTransformerArgs() {
        return transformerArgs;
    }

    public String getTransformerKey() {
        return transformerKey;
    }

    public void setTransformerMap(Map<String, ResponseTransformer> transformerMap) {
        this.transformerMap.putAll(transformerMap);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public interface Listener {
        void onResponse(String uuid, NetworkResponse response);
    }

    public interface ErrorListener {
        void onErrorResponse(String uuid, VolleyError error);
    }
}
