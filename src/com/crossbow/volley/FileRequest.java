package com.crossbow.volley;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by Patrick on 24/03/2015.
 */
abstract class FileRequest<T> implements Comparable<FileRequest>{

    private MarkerLog markerLog = new MarkerLog();

    private boolean canceled;

    /**
     * path to the file on disk
     */
    private String filePath;

    /**
     * Error listener for errors
     */
    private FileResponse.ErrorListener errorListener;

    /**
     * Request number, used by file queue
     */
    private int sequence;

    /**
     * fileQueue the file queue that the request is a part of
     */
    private FileQueue fileQueue;

    /**
     * Tag used to cancel a request
     */
    private Object tag;

    /**
     * The Priority of the request. This is used to order the requests in the queue. Same {@link com.android.volley.Request.Priority Priority}
     */
    public Request.Priority getPriority() {
        return Request.Priority.NORMAL;
    }

    /**
     * Creates a new file request to the file path
     */
    public FileRequest(String filePath, FileResponse.ErrorListener errorListener) {
        if(filePath == null) {
            throw new IllegalArgumentException("File Path cant be null");
        }
        this.errorListener = errorListener;
        this.filePath = filePath;
    }

    public final void preformDelivery(FileResponse<T> response) {
        deliverResult(response.data);
    }


    public final void preformError(VolleyError volleyError) {
        deliverError(volleyError);
    }

    /**
     * The path to the file for this request
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }

    protected void deliverError(VolleyError error) {
        if(errorListener != null) {
            errorListener.onErrorResponse(error);
        }
    }

    /**
     * Mark this request as canceled
     */
    public void cancel() {
        this.canceled = true;
    }

    protected void setFileQueue(FileQueue fileQueue) {
        this.fileQueue = fileQueue;
    }

    /**
     * @return true if a request is canceled
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * @return the tag for this request
     */
    public Object getTag() {
        return tag;
    }

    /**
     * Sets the tag for this request, Used to cancel requests already in flight
     * @param tag the tag to set
     */
    public void setTag(Object tag) {
        this.tag = tag;
    }

    /**
     * set the sequence number (Used for ordering)
     * @param sequence the sequence number
     */
    public void sequence(int sequence) {
        this.sequence = sequence;
    }

    /**
     * get the sequence number
     */
    public int getSequence() {
        return sequence;
    }

    public void mark(String message) {
        markerLog.addMark(message);
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

    protected void finish() {
        VolleyLog.d(getFilePath());
        markerLog.log();
        fileQueue.finishRequest(this);
    }
    
    protected abstract void deliverResult(T result);

    private static class MarkerLog {

        private LinkedList<Marker> markers = new LinkedList<>();
        private static final Object LOCK = new Object();

        public void log() {
            synchronized (LOCK) {

                long startTime = 0;
                for (int i = 0; i < markers.size(); i++) {
                    Marker marker = markers.get(i);
                    if(i == 0) {
                        marker.log(0);
                    }
                    else {
                        double diff = (Math.abs(startTime - marker.time)) / 1000f;
                        marker.log(diff);
                    }
                    startTime = marker.time;
                }
            }
        }

        public void addMark(String message) {

            Marker marker = new Marker(message);
            synchronized (LOCK) {
                markers.add(marker);
            }
        }

        private static class Marker {

            private long time;
            private String msg;

            public Marker(String msg) {
                time = System.nanoTime() / 1000;
                this.msg = msg;
            }

            public void log(double timeDiff) {
                VolleyLog.d("(+%-4.3f)ms %s", timeDiff, msg);
            }
        }
    }
}
