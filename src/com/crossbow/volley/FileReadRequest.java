package com.crossbow.volley;

import com.android.volley.Response;

/**
 * Created by Patrick on 07/04/2015.
 */
public abstract class FileReadRequest<T> extends FileRequest<T> {

    /**
     * Creates a new file request to the file path
     *
     * @param filePath
     * @param errorListener
     */
    public FileReadRequest(String filePath, FileResponse.ErrorListener errorListener) {
        super(filePath, errorListener);
    }

    /**
     * Parse the raw file data
     */
    public final FileResponse<T> doParse(byte[] rawData) {
        return new FileResponse<T>(parseData(rawData));
    }

    /**
     *
     *
     * @param fileData the raw data from the file
     * @return the parsed data
     */
    protected abstract T parseData(byte[] fileData);
}
