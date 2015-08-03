package com.crossbow.volley.mock;

import com.android.volley.VolleyError;
import com.crossbow.volley.FileRequest;
import com.crossbow.volley.FileResponse;
import com.crossbow.volley.toolbox.Files;

import java.io.File;
import java.io.IOException;

/**

 */
public class MockFileRequest extends FileRequest<String> {

    private final FileResponse.Listener<String> listener;

    public MockFileRequest(FileResponse.Listener<String> listener, FileResponse.ErrorListener errorListener) {
        super("", errorListener);
        this.listener = listener;
    }

    public MockFileRequest(String path, FileResponse.Listener<String> listener, FileResponse.ErrorListener errorListener) {
        super(path, errorListener);
        this.listener = listener;
    }

    @Override
    public FileResponse<String> doFileWork(File file) throws VolleyError {
        try {
            return FileResponse.success(new String(Files.readFileData(file)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new VolleyError(e);
        }
    }

    @Override
    protected void deliverResult(String result) {
        if(listener != null) {
            listener.onResponse(result);
        }
    }
}
