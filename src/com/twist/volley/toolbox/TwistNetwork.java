package com.twist.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.HttpStack;

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
public class TwistNetwork extends BasicNetwork {

    public TwistNetwork(HttpStack httpStack) {
        super(httpStack);
    }

    public TwistNetwork(HttpStack httpStack, ByteArrayPool pool) {
        super(httpStack, pool);
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        return super.performRequest(request);
    }
}
