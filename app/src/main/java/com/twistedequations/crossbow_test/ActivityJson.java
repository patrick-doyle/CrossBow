package com.twistedequations.crossbow_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crossbow.volley.toolbox.Crossbow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
        StringRequest stringRequest = new StringRequest("http://www.telize.com/geoip", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                long time = System.currentTimeMillis() - startTime;
                JsonElement jsonElement = jsonParser.parse(response);
                textView.setText("http://www.telize.com/geoip\nin " + time + "ms\n\n" + gson.toJson(jsonElement));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Crossbow.get(this).add(stringRequest);
    }
}
