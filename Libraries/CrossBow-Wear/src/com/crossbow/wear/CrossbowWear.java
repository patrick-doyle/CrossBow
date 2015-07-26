package com.crossbow.wear;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.Volley;
import com.crossbow.volley.CrossbowImageCache;
import com.crossbow.volley.toolbox.Crossbow;
import com.crossbow.volley.toolbox.CrossbowImageLoader;

import java.io.File;


/**
 * Created by Patrick on 11/01/14.
 */
public class CrossbowWear {

    private static Crossbow crossbow;

    private static final String TAG = "Crossbow";

    protected CrossbowWear(Context context) {
    }

    public static com.crossbow.volley.toolbox.Crossbow get(Context context) {
        if(crossbow == null) {

            VolleyLog.setTag(TAG);
            Cache cache = Crossbow.Builder.Default.defaultCache(context);

            PlayNetwork playNetwork = new PlayNetwork(context.getApplicationContext());

            RequestQueue requestQueue = Crossbow.Builder.Default.defaultQueue(cache, playNetwork);
            requestQueue.start();

            CrossbowImageCache crossbowImageCache = Crossbow.Builder.Default.defaultImageCache(context);
            CrossbowImageLoader crossbowImageLoader = Crossbow.Builder.Default.defaultImageLoader(requestQueue, crossbowImageCache);

            crossbow = new Crossbow.Builder(context.getApplicationContext())
                    .setRequestQueue(requestQueue)
                    .setImageLoader(crossbowImageLoader)
                    .build();
        }
        return crossbow;
    }
}
