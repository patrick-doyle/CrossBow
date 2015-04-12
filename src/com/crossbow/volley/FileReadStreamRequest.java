package com.crossbow.volley;

import java.io.InputStream;

/**
 * Created by Patrick on 07/04/2015.
 */
public abstract class FileReadStreamRequest<T> extends FileRequest<T> {

    /**
     * Creates a new file request to the file path
     *
     * @param filePath
     * @param errorListener
     */
    public FileReadStreamRequest(String filePath, FileResponse.ErrorListener errorListener) {
        super(filePath, errorListener);
    }

    /**
     * Parse the raw file data
     * @param inputStream input stream to parse from. Do not close the stream here
     */
    public final FileResponse<T> doParse(InputStream inputStream) {
        return new FileResponse<>(parseData(inputStream));
    }

    /**
     *
     *
     * @param fileData the raw data from the file
     * @return the parsed data
     */
    protected abstract T parseData(InputStream fileData);
}
