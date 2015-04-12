package com.crossbow.volley.toolbox;

import com.android.volley.Response;
import com.crossbow.volley.FileResponse;
import com.crossbow.volley.FileWriteRequest;

/**
 * Created by Patrick on 09/04/2015.
 */
public class StringFileWrite extends FileWriteRequest {

    private final String data;

    /**
     * Creates a new file write request to the file path
     *
     * @param filePath
     * @param errorListener
     */
    public StringFileWrite(String filePath, String data, FileResponse.ErrorListener errorListener, FileResponse.WriteListener listener) {
        super(filePath, errorListener, listener);
        this.data = data;
    }

    @Override
    public byte[] getData() {
        return data.getBytes();
    }
}
