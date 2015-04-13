package com.crossbow.volley.mock;

import com.crossbow.volley.FileReadRequest;
import com.crossbow.volley.FileResponse;

/**
 * Created by Patrick on 12/04/2015.
 */
public class MockRequest extends FileReadRequest<String> {

    public MockRequest(String filePath, FileResponse.ErrorListener errorListener) {
        super(filePath, errorListener);
    }

    @Override
    protected String parseData(byte[] fileData) {
        return null;
    }

    @Override
    protected void deliverResult(String result) {

    }
}
