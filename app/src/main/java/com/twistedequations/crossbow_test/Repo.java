package com.twistedequations.crossbow_test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Patrick on 08/07/2015.
 */
public class Repo {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public int id;

    public String name;

    public String full_name;

    public String description;

    public String default_branch;

    public String html_url;

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
