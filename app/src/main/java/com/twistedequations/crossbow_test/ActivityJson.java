package com.twistedequations.crossbow_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crossbow.gson.GsonGetRequest;
import com.crossbow.volley.toolbox.Crossbow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.twistedequations.crossbow_test.api.RequestRepos;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Patrick on 21/06/2015.
 */
public class ActivityJson extends AppCompatActivity {

    @Bind(R.id.load_stats)
    TextView textView;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private JsonParser jsonParser = new JsonParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_load);
        ButterKnife.bind(this);

        final long startTime = System.currentTimeMillis();
        GsonGetRequest<List<Repo>> requestRepos = new  GsonGetRequest<List<Repo>>("https://api.github.com/users/twistedequations/repos", new Response.Listener<List<Repo>>() {
            @Override
            public void onResponse(List<Repo> response) {
                long time = System.currentTimeMillis() - startTime;
                textView.setText("https://api.github.com/users/twistedequations/repos\nin " + time + "ms\n\n" + response.get(0));
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){};
        Crossbow.get(this).async(requestRepos);
    }
}
