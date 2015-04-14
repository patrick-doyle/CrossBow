package com.crossbow.volley;

import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.android.volley.VolleyError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Patrick on 24/03/2015.
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

                if(fileRequest instanceof FileReadRequest) {
                    FileReadRequest readRequest = (FileReadRequest) fileRequest;
                    byte[] fileData = fileStack.readFileData(readRequest);
                    final FileResponse parsedData = readRequest.doParse(fileData);
                    fileDelivery.deliverSuccess(readRequest, parsedData);
                }
                else if(fileRequest instanceof FileReadStreamRequest) {
                    FileReadStreamRequest streamRequest = (FileReadStreamRequest) fileRequest;
                    InputStream stream = fileStack.readFileStream(streamRequest);
                    final FileResponse parsedData = streamRequest.doParse(stream);
                    stream.close();
                    fileDelivery.deliverSuccess(streamRequest, parsedData);
                }
                else if(fileRequest instanceof FileWriteRequest) {
                    FileWriteRequest writeRequest = (FileWriteRequest) fileRequest;
                    fileStack.writeFileData(writeRequest);
                    final FileResponse<Boolean> parsedData = new FileResponse<>(true);
                    fileDelivery.deliverSuccess(writeRequest, parsedData);
                }
                else if(fileRequest instanceof FileWriteStreamRequest) {
                    FileWriteStreamRequest writeRequest = (FileWriteStreamRequest) fileRequest;
                    OutputStream outputStream = fileStack.writeFileStream(writeRequest);
                    writeRequest.writeData(outputStream);
                    final FileResponse<Boolean> parsedData = new FileResponse<>(true);
                    outputStream.close();
                    fileDelivery.deliverSuccess(writeRequest, parsedData);
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
