package com.crossbow.wear.core;

import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class WearDataRequestTest extends TestCase {

    private String uuid = UUID.randomUUID().toString();

    private String url = UUID.randomUUID().toString();

    private String transformerKey = UUID.randomUUID().toString();

    private String cacheKey = UUID.randomUUID().toString();

    private String tag = UUID.randomUUID().toString();

    private String bodyContentType = UUID.randomUUID().toString();

    private Map<String, String> headers = new HashMap<>();

    private byte[] body = UUID.randomUUID().toString().getBytes();

    private Request.Priority priority = Request.Priority.HIGH;

    private ParamsBundle transformerArgs = new ParamsBundle();

    private RetryPolicy retryPolicy;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        transformerArgs.putString("arg1", UUID.randomUUID().toString());
        transformerArgs.putString("arg2", UUID.randomUUID().toString());

        headers.put("arg1", UUID.randomUUID().toString());
        headers.put("arg2", UUID.randomUUID().toString());
        retryPolicy = new DefaultRetryPolicy(3685, 7, 0.4f);
    }

    @SmallTest
    public void testDataRequestMapping() throws AuthFailureError {
        WearDataRequest wearDataRequest = new WearDataRequest(Request.Method.GET,
                url, uuid, transformerKey, retryPolicy.getCurrentRetryCount(),
                retryPolicy.getCurrentTimeout(),
                cacheKey, tag, bodyContentType, headers, body, priority, transformerArgs);

        assertEquals(cacheKey, wearDataRequest.getCacheKey());
        assertEquals(transformerArgs, wearDataRequest.getTransformerArgs());
        assertEquals(priority, wearDataRequest.getPriority());
        assertEquals(body, wearDataRequest.getBody());
        assertEquals(headers, wearDataRequest.getHeaders());
        assertEquals(bodyContentType, wearDataRequest.getBodyContentType());
        assertEquals(tag, wearDataRequest.getTag());
        assertEquals(cacheKey, wearDataRequest.getCacheKey());
        assertEquals(retryPolicy.getCurrentTimeout(), wearDataRequest.getTimeoutMs());
        assertEquals(retryPolicy.getCurrentRetryCount(), wearDataRequest.getRetryPolicy().getCurrentRetryCount());
        assertEquals(transformerKey, wearDataRequest.getTransformerKey());
        assertEquals(url, wearDataRequest.getUrl());
        assertEquals(uuid, wearDataRequest.getUuid());
    }
}
