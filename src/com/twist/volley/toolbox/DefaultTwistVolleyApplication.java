package com.twist.volley.toolbox;

import android.app.Application;

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
