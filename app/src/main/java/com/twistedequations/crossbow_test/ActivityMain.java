package com.twistedequations.crossbow_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.asset_image_load, R.id.json_load, R.id.network_image_load, R.id.network_image_list, R.id.compare_loaders})
    public void OnItemClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.asset_image_load:
                intent = new Intent(this, ActivityAssetLoad.class);
                startActivity(intent);
                break;

            case R.id.json_load:
                intent = new Intent(this, ActivityJson.class);
                startActivity(intent);
                break;

            case R.id.network_image_load:
                intent = new Intent(this, ActivityNetworkImageLoad.class);
                startActivity(intent);
                break;

            case R.id.network_image_list:
                intent = new Intent(this, ActivityImageList.class);
                startActivity(intent);
                break;

            case R.id.compare_loaders:
                intent = new Intent(this, ActivityComapreLibs.class);
                startActivity(intent);
                break;
        }
    }
}
