package com.twistedequations.crossbow_wear_test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crossbow.volley.CrossbowImage;
import com.crossbow.wear.CrossbowWear;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityDetails extends Activity {

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.string_response)
    TextView textView;

    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        makeStringRequest();
    }

    private void makeStringRequest() {
        progressBar.setVisibility(View.VISIBLE);
        textView.setText(null);
        Log.d("Activity", "Sending request");
        RepoNameRequest repoNameRequest = new RepoNameRequest("https://api.github.com/users/twistedequations/repos", new Response.Listener<List<String>>() {
            @Override
            public void onResponse(List<String> response) {
                Log.d("Activity", "response = " + response);
                progressBar.setVisibility(View.GONE);
                textView.setText(TextUtils.join("\n", response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Activity", "error = " + error);
                progressBar.setVisibility(View.GONE);
                textView.setText("error = " + error);
            }
        });
        CrossbowWear.get(this).add(repoNameRequest);
    }

}
