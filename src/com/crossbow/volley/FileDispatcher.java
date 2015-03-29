package com.crossbow.volley;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Patrick on 24/03/2015.
 */
public class FileDispatcher extends Thread {

    private BlockingQueue<FileRequest<?>> requestQueue;

    private FileReader fileReader;
    private FileDelivery fileDelivery;

    private boolean run = true;
    private boolean isRunning = false;

    public FileDispatcher(@NonNull BlockingQueue<FileRequest<?>> requestQueue, @NonNull FileReader fileReader, FileDelivery fileDelivery) {
        this.requestQueue = requestQueue;
        this.fileReader = fileReader;
        this.fileDelivery = fileDelivery;
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
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (!run) {
                    return;
                }
                continue;
            }

            try {

                byte[] fileData = fileReader.readFile(fileRequest);

                final FileResponse parsedData = fileRequest.doParse(fileData);
                if(!run) {
                    return;
                }

                fileDelivery.deliverSuccess(fileRequest, parsedData);
            }
            catch (final VolleyError volleyError) {
                if(!run) {
                    return;
                }
                fileDelivery.deliverError(fileRequest, volleyError);
            }
            catch (final Exception e) {
                if(!run) {
                    return;
                }
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
