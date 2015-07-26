package com.twistedequations.crossbow_test.api;

import com.twistedequations.crossbow_test.Repo;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Patrick on 08/07/2015.
 */
public interface GithubService {

    @GET("/users/twistedequations/repos")
    void listRepos(Callback<List<Repo>> callback);
}
