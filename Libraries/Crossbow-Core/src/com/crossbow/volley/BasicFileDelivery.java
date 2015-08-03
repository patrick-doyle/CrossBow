package com.crossbow.volley;

import android.os.Handler;
import android.os.Looper;

import com.android.volley.VolleyError;

/**

 */
public class BasicFileDelivery implements FileDelivery {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void deliverSuccess(FileRequest<?> fileRequest, FileResponse<?> fileResponse) {

        if(!fileRequest.isCanceled()) {
            fileRequest.mark("post-result");
            fileRequest.finish();
            handler.post(new SuccessRunnable(fileRequest, fileResponse));
        }
        else {
            fileRequest.mark("result-canceled-at-delivery");
        }
    }

    @Override
    public void deliverError(FileRequest<?> fileRequest, VolleyError error) {
        if(!fileRequest.isCanceled()) {
            fileRequest.mark("post-error");
            fileRequest.finish();
            handler.post(new ErrorRunnable(fileRequest, error));
        }
        else {
            fileRequest.mark("error-canceled-at-delivery");
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
