package com.crossbow.volley.toolbox;

import android.os.Handler;
import android.os.Looper;

import com.android.volley.VolleyError;
import com.crossbow.volley.FileDelivery;
import com.crossbow.volley.FileRequest;
import com.crossbow.volley.FileResponse;

/**
 * Created by Patrick on 29/03/2015.
 */
public class BasicFileDelivery implements FileDelivery {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void deliverSuccess(FileRequest<?> fileRequest, FileResponse<?> fileResponse) {

        if(!fileRequest.isCanceled()) {
            handler.post(new SuccessRunnable(fileRequest, fileResponse));
        }
    }

    @Override
    public void deliverError(FileRequest<?> fileRequest, VolleyError error) {
        if(!fileRequest.isCanceled()) {
            handler.post(new ErrorRunnable(fileRequest, error));
        }
    }

    private class SuccessRunnable implements Runnable {

        private final FileRequest<?> fileRequest;
        private final FileResponse fileResponse;

        private SuccessRunnable(FileRequest<?> fileRequest, FileResponse<?> fileResponse) {
            this.fileRequest = fileRequest;
            this.fileResponse = fileResponse;
        }

        @Override
        public void run() {
            fileRequest.preformDelivery(fileResponse);
        }
    }

    private class ErrorRunnable implements Runnable {

        private final FileRequest<?> fileRequest;
        private final VolleyError volleyError;

        private ErrorRunnable(FileRequest<?> fileRequest, VolleyError volleyError) {
            this.fileRequest = fileRequest;
            this.volleyError = volleyError;
        }

        @Override
        public void run() {
            fileRequest.preformError(volleyError);
        }
    }
}
