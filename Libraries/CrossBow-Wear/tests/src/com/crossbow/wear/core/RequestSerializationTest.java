package com.crossbow.wear.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.crossbow.wear.BuildConfig;
import com.crossbow.wear.core.WearDataRequest;
import com.crossbow.wear.core.RequestSerialUtil;
import com.crossbow.wear.core.ParamsBundle;
import com.crossbow.wear.core.WearRequest;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**

 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class RequestSerializationTest {

    @Test
    @SmallTest
    public void testRequestSerialization() throws AuthFailureError, IOException {
        String uuid = UUID.randomUUID().toString();
        TestRequest testRequest = new TestRequest();
        byte[] serializedRequest = com.crossbow.wear.core.RequestSerialUtil.serializeRequest(uuid, testRequest);

        com.crossbow.wear.core.WearDataRequest dataRequest = RequestSerialUtil.deSerializeRequest(serializedRequest);

        Assert.assertTrue(Arrays.equals(testRequest.getBody(), dataRequest.getBody()));
        Assert.assertTrue(equalBundles(testRequest.getTransformerParams(), dataRequest.getTransformerArgs()));
        Assert.assertEquals(testRequest.getCacheKey(), dataRequest.getCacheKey());
        Assert.assertEquals(testRequest.getUrl(), dataRequest.getUrl());
        Assert.assertEquals(testRequest.getBodyContentType(), dataRequest.getBodyContentType());
        Assert.assertEquals(testRequest.getPriority(), dataRequest.getPriority());
        Assert.assertEquals(testRequest.getTransFormerKey(), dataRequest.getTransformerKey());
        Assert.assertEquals(testRequest.getMethod(), dataRequest.getMethod());
        Assert.assertEquals(testRequest.getHeaders(), dataRequest.getHeaders());
        Assert.assertEquals(testRequest.getRetryPolicy().getCurrentRetryCount(), dataRequest.getRetryPolicy().getCurrentRetryCount());
        Assert.assertEquals(testRequest.getRetryPolicy().getCurrentTimeout(), dataRequest.getRetryPolicy().getCurrentTimeout());
    }

    public boolean equalBundles(ParamsBundle one, ParamsBundle two) {
        if(one.size() != two.size())
            return false;

        Set<String> setOne = one.keySet();
        Object valueOne;
        Object valueTwo;

        for(String key : setOne) {
            valueOne = one.get(key);
            valueTwo = two.get(key);
            if(valueOne == null) {
                if(valueTwo != null || !two.containsKey(key))
                    return false;
            }
            else if(!valueOne.equals(valueTwo))
                return false;
        }

        return true;
    }

    private class TestRequest extends Request<Boolean> implements WearRequest {

        public TestRequest() {
            super(Method.PATCH, "https://api.github.com/users/twistedequations/repos", null);
            setRetryPolicy(new DefaultRetryPolicy(3600, 23, 2.0f));
        }

        @Override
        protected Response<Boolean> parseNetworkResponse(NetworkResponse response) {return null;}

        @Override
        protected void deliverResponse(Boolean response) {}

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept-Encoding", "gzip");
            headers.put("cache-control", "private, max-age=31536000");
            headers.put("expires", "Mon, 20 Jul 2015 21:21:22 GMT");
            return headers;
        }

        @Override
        public String getBodyContentType() {
            return "body-content-type";
        }

        @Override
        public String getCacheKey() {
            return "big-cache-key";
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            return new byte[]{3,56,0x4,56,98,0x73};
        }

        @Override
        public Priority getPriority() {
            return Priority.HIGH;
        }

        @NonNull
        @Override
        public ParamsBundle getTransformerParams() {
            ParamsBundle bundle = new ParamsBundle();
            bundle.putInt("width", 1080);
            return bundle;
        }

        @Nullable
        @Override
        public String getTransFormerKey() {
            return "image_transformer";
        }
    }

}
