package com.crossbow.volley.toolbox;

import com.crossbow.volley.FileReadRequest;
import com.crossbow.volley.FileResponse;

/**
 * Created by Patrick on 26/03/2015.
 */
public class StringFileRequest extends FileReadRequest<String> {

    private final FileResponse.ReadListener<String> listener;

    public StringFileRequest(String filePath, FileResponse.ErrorListener errorListener, FileResponse.ReadListener<String> listener) {
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
