package com.twist.volley.toolbox;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.twist.volley.TwistVolleyApplication;
import com.twist.volley.VolleyStack;

/**
 * Created by Patrick on 04/08/2014.
 */
public class TwistVolley {

    private static TwistVolley twistVolley;
    private VolleyStack volleyStack;

    public static TwistVolley from(Context context) {

        if(twistVolley == null) {
            twistVolley = new TwistVolley(context);
        }
        return twistVolley;
    }

    private TwistVolley (Context context) {
        try {
            TwistVolleyApplication application = (TwistVolleyApplication) context.getApplicationContext();
            this.volleyStack = application.getStack();
        }
        catch (ClassCastException e) {
            this.volleyStack = new DefaultVolleyStack(context);
        }
    }

    public final void addRequest(Request request) {
        volleyStack.getRequestQueue().add(request);
    }

    public final RequestQueue getRequestQueue() {
        return volleyStack.getRequestQueue();
    }

    public final ImageLoader getImageLoader() {
        return volleyStack.getImageLoader();
    }

    public final ImageLoader.ImageCache getImageCache() {
        return volleyStack.getImageCache();
    }

}
