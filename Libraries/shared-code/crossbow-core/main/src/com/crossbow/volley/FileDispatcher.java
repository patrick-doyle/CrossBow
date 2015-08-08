package com.crossbow.volley;

import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.android.volley.VolleyError;

import java.util.concurrent.BlockingQueue;

/**

 */
public class FileDispatcher extends Thread {

    private BlockingQueue<FileRequest<?>> requestQueue;

    private FileStack fileStack;
    private FileDelivery fileDelivery;

    private boolean run = true;
    private boolean isRunning = false;

    public FileDispatcher(@NonNull BlockingQueue<FileRequest<?>> requestQueue, @NonNull FileStack fileStack, FileDelivery fileDelivery) {
        this.requestQueue = requestQueue;
        this.fileStack = fileStack;
        this.fileDelivery = fileDelivery;
        setName("Crossbow File Thread");
    }

    @Override
    public synchronized void start() {
        run = true;
        if(isRunning) {
            //prevent recreating a new thread
            return;
        }
        isRunning = true;
        super.start();
    }

    @Override
    public void run() {
        super.run();

        while(run) {

            long startTimeMs = SystemClock.elapsedRealtime();

            final FileRequest<?> fileRequest;
            try {
                // Take a request from the queue.
                fileRequest = requestQueue.take();

                if(fileRequest.isCanceled()) {
                    //canceled bin request
                    fileRequest.mark("canceled-at-stack");
                    fileRequest.finish();
                    continue;
                }
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (!run) {
                    return;
                }
                continue;
            }

            try {
                fileRequest.mark("file-operation-start");
                final FileResponse parsedData = fileStack.performFileOperation(fileRequest);
                if(parsedData == null) {
                    fileDelivery.deliverError(fileRequest, new VolleyError("FileResponse from request was null"));
                }
                else if (parsedData.isError()) {
                    fileDelivery.deliverError(fileRequest, parsedData.e);
                }
                else{
                    fileDelivery.deliverSuccess(fileRequest, parsedData);
                }
            }
            catch (final VolleyError volleyError) {
                fileDelivery.deliverError(fileRequest, volleyError);
            }
            catch (final Exception e) {
                fileDelivery.deliverError(fileRequest, new VolleyError(e));
            }
        }
    }

    public void quit() {
        run = false;
        isRunning = false;
        interrupt();
    }
}
