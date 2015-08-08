package com.crossbow.wear.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**

 */
public class RequestSerializationTest extends TestCase {

    @SmallTest
    public void testRequestSerialization() throws AuthFailureError, IOException {
        String uuid = UUID.randomUUID().toString();
        TestRequest testRequest = new TestRequest();
        byte[] serializedRequest = RequestSerialUtil.serializeRequest(uuid, testRequest);

        WearDataRequest dataRequest = RequestSerialUtil.deSerializeRequest(serializedRequest);

        assertTrue(Arrays.equals(testRequest.getBody(), dataRequest.getBody()));
        assertTrue(equalBundles(testRequest.getTransformerParams(), dataRequest.getTransformerArgs()));
        assertEquals(testRequest.getCacheKey(), dataRequest.getCacheKey());
        assertEquals(testRequest.getUrl(), dataRequest.getUrl());
        assertEquals(testRequest.getBodyContentType(), dataRequest.getBodyContentType());
        assertEquals(testRequest.getPriority(), dataRequest.getPriority());
        assertEquals(testRequest.getTransFormerKey(), dataRequest.getTransformerKey());
        assertEquals(testRequest.getMethod(), dataRequest.getMethod());
        assertEquals(testRequest.getHeaders(), dataRequest.getHeaders());
        assertEquals(testRequest.getRetryPolicy().getCurrentRetryCount(), dataRequest.getRetryPolicy().getCurrentRetryCount());
        assertEquals(testRequest.getRetryPolicy().getCurrentTimeout(), dataRequest.getRetryPolicy().getCurrentTimeout());
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
