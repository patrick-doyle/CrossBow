package com.twist.volley.toolbox;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.twist.volley.TwistVolleyApplication;
import com.twist.volley.VolleyStack;

/*
 * Copyright (C) 2014 Patrick Doyle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
