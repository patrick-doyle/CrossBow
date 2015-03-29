package com.crossbow.volley.mock;

import com.android.volley.Response;
import com.crossbow.volley.FileRequest;

/**
 * Created by Patrick on 29/03/2015.
 */
public class MockFileRequest extends FileRequest<String> {

    private final Response.Listener<String> listener;

    public MockFileRequest(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(null, errorListener);
        this.listener = listener;
    }

    @Override
    protected String parseData(byte[] fileData) {
        return null;
    }

    @Override
    protected void deliverResult(String result) {
        listener.onResponse(result);
    }
}
