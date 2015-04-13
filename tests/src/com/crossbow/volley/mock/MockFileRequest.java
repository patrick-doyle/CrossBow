package com.crossbow.volley.mock;

import com.android.volley.Response;
import com.crossbow.volley.FileReadRequest;
import com.crossbow.volley.FileResponse;

/**
 * Created by Patrick on 29/03/2015.
 */
public class MockFileRequest extends FileReadRequest<String> {

    private final FileResponse.ReadListener<String> listener;

    public MockFileRequest(FileResponse.ReadListener<String> listener, FileResponse.ErrorListener errorListener) {
        super("", errorListener);
        this.listener = listener;
    }

    public MockFileRequest(String path, FileResponse.ReadListener<String> listener, FileResponse.ErrorListener errorListener) {
        super(path, errorListener);
        this.listener = listener;
    }

    @Override
    protected String parseData(byte[] fileData) {
        return new String(fileData);
    }

    @Override
    protected void deliverResult(String result) {
        if(listener != null) {
            listener.onResponse(result);
        }
    }
}
