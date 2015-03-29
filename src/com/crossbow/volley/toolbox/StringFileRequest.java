package com.crossbow.volley.toolbox;

import com.android.volley.Response;
import com.crossbow.volley.FileRequest;

/**
 * Created by Patrick on 26/03/2015.
 */
public class StringFileRequest extends FileRequest<String> {

    private final Response.Listener<String> listener;

    public StringFileRequest(String filePath, Response.ErrorListener errorListener, Response.Listener<String> listener) {
        super(filePath, errorListener);
        this.listener = listener;
    }

    @Override
    protected String parseData(byte[] fileData) {
        return new String(fileData);
    }

    @Override
    protected void deliverResult(String result) {
        listener.onResponse(result);
    }
}
