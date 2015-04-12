package com.crossbow.volley;

import com.android.volley.Response;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Patrick on 07/04/2015.
 */
public abstract class FileWriteStreamRequest extends FileRequest<Boolean> {

    private final FileResponse.WriteListener writeListener;

    /**
     * Creates a new file write request to the file path
     *
     * @param filePath pat
     * @param errorListener
     */
    public FileWriteStreamRequest(String filePath, FileResponse.ErrorListener errorListener, FileResponse.WriteListener writeListener) {
        super(filePath, errorListener);
        this.writeListener = writeListener;
    }

    /**
     * Write the data to file output stream to create the file
     */
    protected abstract void writeData(OutputStream outputStream);

    @Override
    protected void deliverResult(Boolean result) {
        if(writeListener != null) {
            writeListener.onResponse();
        }
    }
}
