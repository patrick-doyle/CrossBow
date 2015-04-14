package com.crossbow.volley;

/**
 * Created by Patrick on 07/04/2015.
 */
public abstract class FileWriteRequest extends FileRequest<Boolean> {

    private final FileResponse.WriteListener writeListener;

    /**
     * Creates a new file write request to the file path
     *
     * @param filePath
     * @param errorListener
     */
    public FileWriteRequest(String filePath, FileResponse.ErrorListener errorListener, FileResponse.WriteListener writeListener) {
        super(filePath, errorListener);
        this.writeListener = writeListener;
    }

    /**
     * @return the raw byte stream to write to the file
     */
    public abstract byte[] getData();

    @Override
    protected void deliverResult(Boolean result) {
        if(writeListener != null) {
            writeListener.onResponse();
        }
    }
}
