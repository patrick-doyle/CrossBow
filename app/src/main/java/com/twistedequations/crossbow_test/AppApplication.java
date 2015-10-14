package com.twistedequations.crossbow_test;

import android.app.Application;

import com.crossbow.volley.toolbox.Crossbow;

public class AppApplication extends Application {

    Crossbow crossbow;

    @Override
    public void onCreate() {
        super.onCreate();
        crossbow = Crossbow.get(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        crossbow.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        crossbow.onTrimMemory(level);
    }
}
