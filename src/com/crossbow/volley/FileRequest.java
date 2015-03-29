package com.crossbow.volley;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.File;

/**
 * Created by Patrick on 24/03/2015.
 */
public abstract class FileRequest<T> implements Comparable<FileRequest>{

    private boolean canceled;

    private String filePath;

    private Response.ErrorListener errorListener;

    private int sequence;

    public Request.Priority getPriority() {
        return Request.Priority.NORMAL;
    }

    public FileRequest(String filePath, Response.ErrorListener errorListener) {
        this.errorListener = errorListener;
        this.filePath = filePath;
    }

    public FileResponse<T> doParse(byte[] rawData) {
        FileResponse<T> response = new FileResponse<T>();
        response.data = parseData(rawData);
        return response;
    }

    public void preformDelivery(FileResponse<T> response) {
        deliverResult(response.data);
    }


    public void preformError(VolleyError volleyError) {
        deliverError(volleyError);
    }

    public String getFilePath() {
        return filePath;
    }

    protected void deliverError(VolleyError error) {
        if(errorListener != null) {
            errorListener.onErrorResponse(error);
        }
    }

    public void setCanceled(boolean isCanceled) {
        this.canceled = isCanceled;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void sequence(int sequence) {
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
    }

    @Override
    public int compareTo(@NonNull FileRequest another) {
        Request.Priority left = this.getPriority();
        Request.Priority right = another.getPriority();

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return left == right ?
                getSequence() - another.getSequence() :
                right.ordinal() - left.ordinal();
    }

    protected abstract T parseData(byte[] fileData);

    protected abstract void deliverResult(T result);
}
