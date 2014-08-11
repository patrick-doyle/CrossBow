package com.twist.volley.toolbox;

import android.app.Application;

import com.twist.volley.TwistVolleyApplication;
import com.twist.volley.VolleyStack;

/**
 * Created by Patrick on 04/08/2014.
 */
public class DefaultTwistVolleyApplication extends Application implements TwistVolleyApplication {

    private DefaultVolleyStack volleyStack;

    @Override
    public void onCreate() {
        super.onCreate();
        volleyStack = new DefaultVolleyStack(this);
    }

    @Override
    public VolleyStack getStack() {
        return volleyStack;
    }
}
