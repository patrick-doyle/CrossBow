package com.twist.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.HttpStack;

/**
 * Created by Patrick on 10/08/2014.
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
