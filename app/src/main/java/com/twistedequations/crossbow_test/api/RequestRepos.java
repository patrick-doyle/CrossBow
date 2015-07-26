package com.twistedequations.crossbow_test.api;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.twistedequations.crossbow_test.Repo;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Patrick on 08/07/2015.
 */
public class RequestRepos extends Request<List<Repo>> {

    private final Response.Listener<List<Repo>> listListener;

    public RequestRepos(String url, Response.Listener<List<Repo>> listListener, Response.ErrorListener listener) {
        super(Method.GET, url, listener);
        this.listListener = listListener;
    }

    @Override
    protected Response<List<Repo>> parseNetworkResponse(NetworkResponse response) {
        Type type = new TypeToken<List<Repo>>(){}.getType();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(response.data)));
        List<Repo> repos = new Gson().fromJson(jsonReader, type);
        return Response.success(repos, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(List<Repo> response) {
        listListener.onResponse(response);
    }
}
