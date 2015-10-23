package com.twistedequations.crossbow_wear_test;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick on 26/07/2015.
 */
public class RepoNameRequest extends Request<List<String>> {

    private Response.Listener<List<String>> listListener;

    public RepoNameRequest(String url, Response.Listener<List<String>> listListener, Response.ErrorListener listener) {
        super(Method.GET, url, listener);
        this.listListener = listListener;
    }

    @Override
    protected Response<List<String>> parseNetworkResponse(NetworkResponse response) {
        Gson gson = new Gson();

        String body = new String(response.data);
        Type type = new TypeToken<List<Repo> >(){}.getType();
        List<Repo> repos = gson.fromJson(body, type);

        List<String> repoNames = new ArrayList<>(repos.size());
        for(Repo repo : repos) {
            repoNames.add(repo.name);
        }
        return Response.success(repoNames, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(List<String> response) {
        listListener.onResponse(response);
    }
}
