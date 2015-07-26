package com.twistedequations.crossbow_test;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.crossbow.volley.CrossbowImage;
import com.crossbow.volley.toolbox.Crossbow;
import com.squareup.picasso.Picasso;
import com.twistedequations.crossbow_test.api.GithubService;
import com.twistedequations.crossbow_test.api.RequestRepos;

import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Patrick on 07/07/2015.
 */
public class ActivityComapreLibs extends AppCompatActivity {

    @Bind(R.id.load_stats)
    TextView loadStats;

    @Bind(R.id.load_data)
    TextView loadData;

    @Bind(R.id.imageview)
    ImageView imageView;

    private long start;

    private RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint("https://api.github.com")
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_libs);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.load_crossbow, R.id.load_retofit, R.id.load_pisacco, R.id.load_glide, R.id.load_crossbow_image})
    public void ocClick(View view) {
        start = System.currentTimeMillis();
        switch (view.getId()) {
            case R.id.load_crossbow:
                showData();
                loadCrossbow();
                break;

            case R.id.load_retofit:
                showData();
                loadRetrofit();
                break;

            case R.id.load_pisacco:
                showImage();
                loadPissaco();
                break;

            case R.id.load_glide:
                showImage();
                loadGlide();
                break;

            case R.id.load_crossbow_image:
                showImage();
                loadCrossBowImage();
                break;
        }
    }

    private void showStats() {
        long timeTaken = System.currentTimeMillis() - start;
        loadStats.setText(String.format(Locale.ENGLISH, "Load took %d ms", timeTaken));
    }

    private void showToast() {
        Toast.makeText(this, "Error Loading", Toast.LENGTH_SHORT).show();
    }

    private void showImage() {
        imageView.setVisibility(View.VISIBLE);
        loadData.setVisibility(View.GONE);
    }

    private void showData() {
        imageView.setVisibility(View.GONE);
        loadData.setVisibility(View.VISIBLE);
    }

    private void loadRetrofit() {

        GithubService service = restAdapter.create(GithubService.class);
        service.listRepos(new Callback<List<Repo>>() {
            @Override
            public void success(List<Repo> repo, Response response) {
                loadData.setText(repo.toString());
                showStats();
            }

            @Override
            public void failure(RetrofitError error) {
                showToast();
            }
        });
    }

    private void loadCrossbow() {
        RequestRepos requestRepos = new RequestRepos("https://api.github.com/users/twistedequations/repos", new com.android.volley.Response.Listener<List<Repo>>() {
            @Override
            public void onResponse(List<Repo> response) {
                showStats();
                loadData.setText(response.toString());
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast();
            }
        });
        Crossbow.get(this).add(requestRepos);
    }

    private void loadCrossBowImage() {
        Crossbow.get(this).loadImage().source("http://i.imgur.com/ByktT4N.jpg").centerCrop().listen(new CrossbowImage.Listener() {
            @Override
            public void onLoad(boolean success, boolean fromCache, Bitmap bitmap, ImageView imageView) {
                if (success) {
                    showStats();
                }
                else {
                    showToast();
                }
            }
        }).into(imageView);
    }

    private void loadPissaco() {
        Picasso.with(this).load("http://i.imgur.com/ByktT4N.jpg").noFade().into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                showStats();
            }

            @Override
            public void onError() {
                showToast();
            }
        });
    }

    private void loadGlide() {
        Glide.with(this).load("http://i.imgur.com/ByktT4N.jpg").listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                showToast();
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                showStats();
                return false;
            }
        }).into(imageView);
    }
}
