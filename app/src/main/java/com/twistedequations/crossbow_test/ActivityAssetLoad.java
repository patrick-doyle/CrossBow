package com.twistedequations.crossbow_test;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;


import com.crossbow.volley.CrossbowImage;
import com.crossbow.volley.toolbox.Crossbow;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Patrick on 21/06/2015.
 */
public class ActivityAssetLoad extends AppCompatActivity {

    @Bind(R.id.load_stats)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_load);
        ButterKnife.bind(this);

        final long startTime = System.currentTimeMillis();

        Crossbow.get(this).loadImage().source("test-image.jpg").fade(200).listen(new CrossbowImage.Listener() {
            @Override
            public void onLoad(boolean success, boolean fromCache, Bitmap bitmap, ImageView imageView) {
                if (success) {

                    long time = System.currentTimeMillis() - startTime;
                    String text = String.format(Locale.ENGLISH, "Loaded Bitmap size - %dw, %dh\n" +
                                    "from cache - %b\n" +
                                    "into ImageView sized -%dw, %dh\n" +
                                    "in %dms",
                            bitmap.getWidth(), bitmap.getHeight(), fromCache, imageView.getWidth(), imageView.getHeight(), time);
                    textView.setText(text);
                }
                else {
                    textView.setText("Error");
                }
            }
        }).into(this, R.id.asset_image);
    }
}
