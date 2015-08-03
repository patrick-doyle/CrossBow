package com.crossbow.wear.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**

 */
public class RequestSerializationCompareTest extends TestCase {

    @SmallTest
    public void testRequestSerializationSize() throws AuthFailureError, IOException {
        String uuid = UUID.randomUUID().toString();
        TestRequest testRequest = new TestRequest();
        byte[] serializedRequest = RequestSerialUtil.serializeRequest(uuid, testRequest);
        byte[] serializedRequestOld = RequestSerialUtilOld.serializeRequest(uuid, testRequest);

        Log.d("TEST", "New size = " + serializedRequest.length + ", old size = " + serializedRequestOld.length);
    }

    @SmallTest
    public void testRequestSerializationSpeed() throws AuthFailureError, IOException {
        String uuid = UUID.randomUUID().toString();
        TestRequest testRequest = new TestRequest();
        long start = System.nanoTime();

        byte[] serializedRequestOld = RequestSerialUtilOld.serializeRequest(uuid, testRequest);

        long startOld = System.nanoTime();
        Log.d("TEST", "Old time = " + ((System.nanoTime() - startOld)) + "ns");

        byte[] serializedRequest = RequestSerialUtil.serializeRequest(uuid, testRequest);

        Log.d("TEST", "New time = " + ((System.nanoTime() - start)) + "ns");
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
