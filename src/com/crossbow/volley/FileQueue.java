package com.crossbow.volley;

import android.content.ContentValues;
import android.content.Context;

import com.crossbow.volley.toolbox.BasicFileStack;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Patrick on 24/03/2015.
 *
 * The file queue handles the creation of a queue and the dispatching of the file read/writes to separate threads. This is run independently of the normal volley request queue.
 *
 * The default thread count is 3.
 */
public class FileQueue {

    private PriorityBlockingQueue<FileRequest<?>>  requestQueue = new PriorityBlockingQueue<>();

    private FileStack fileStack;

    private List<FileRequest> inFlightRequests = new LinkedList<>();

    private AtomicInteger sequenceNumber = new AtomicInteger();

    private static final int THREAD_COUNT = 3;

    private FileDispatcher[] fileDispatchers = new FileDispatcher[THREAD_COUNT];

    private FileDelivery fileDelivery;

    private boolean running = false;

    private Map<String, LinkedList<FileRequest<?>>> blockedRequests = new HashMap<>();

    private Set<String> inUseFiles = new HashSet<>();

    /**
     * Creates a new file queue
     * @param fileDelivery the File deliver to use
     * @param fileStack the file stack to use for reading a writing
     * @param threadCount the size of the thread pool. Defaults to 3.
     */
    public FileQueue(FileDelivery fileDelivery, FileStack fileStack, int threadCount) {
        this.fileDelivery = fileDelivery;
        this.fileStack = fileStack;
        fileDispatchers = new FileDispatcher[threadCount];
    }

    /**
     * Creates a new file queue with the default thread pool size of 3
     */
    public FileQueue(FileDelivery fileDelivery, FileStack fileStack) {
        this(fileDelivery, fileStack, THREAD_COUNT);
    }

    /**
     * Creates a new file queue with the default File delivery, Reader and thread pool size.
     */
    public FileQueue(Context context) {
        this(new BasicFileDelivery(), new BasicFileStack(context), THREAD_COUNT);
    }

    /**
     * Adds a request to the queue. {@link #start()} has to be called inorder for requests to be processed
     * @param fileRequest
     */
    public void add(FileRequest fileRequest) {

        if(fileRequest == null) {
            return;
        }

        //Set the sequence number
        int sequence = sequenceNumber.incrementAndGet();
        fileRequest.sequence(sequence);

        fileRequest.setFileQueue(this);

        String filePath = fileRequest.getFilePath();

        synchronized (inUseFiles) {
            if(inUseFiles.contains(filePath)) {

                if(blockedRequests.containsKey(filePath)) {
                    List<FileRequest<?>> list = blockedRequests.get(filePath);
                    list.add(fileRequest);
                }
                else {
                    LinkedList<FileRequest<?>> list = new LinkedList<>();
                    list.add(fileRequest);
                    blockedRequests.put(filePath, list);
                }
                fileRequest.mark("file-in-use-blocking");
            }
            else {
                inUseFiles.add(filePath);

                //add to the inFlight requests
                synchronized (inFlightRequests) {
                    inFlightRequests.add(fileRequest);
                }
                requestQueue.add(fileRequest);
                fileRequest.mark("add-to-queue");
            }
        }
    }

    /**
     * Start the request queue processing requests
     */
    public void start() {
        if(running) {
            return;
        }
        running = true;

        for (int i = 0; i < fileDispatchers.length; i++) {
            fileDispatchers[i] = new FileDispatcher(requestQueue, fileStack, fileDelivery);
            fileDispatchers[i].start();
        }
    }

    /**
     * Stops the queue from processing any new requests
     */
    public void stop() {
        running = false;
        for (int i = 0; i < fileDispatchers.length; i++) {
            fileDispatchers[i] = new FileDispatcher(requestQueue, fileStack, fileDelivery);
            fileDispatchers[i].quit();
        }
    }

    /**
     * Cancels any requests that match the current request filter
     * @param filter FileRequestFilter to use to cancel the requests
     */
    public void cancelAll(FileRequestFilter filter) {
        synchronized (inFlightRequests) {
            for(FileRequest<?> request : inFlightRequests) {
                if(filter.apply(request)) request.cancel();
            }
        }
    }

    /**
     * Cancels every request  in the queue
     */
    public void cancelAll() {
        synchronized (inFlightRequests) {
            for(FileRequest<?> request : inFlightRequests) {
                request.cancel();
            }
        }
    }

    /**
     * Cancel all the requests that have a matching tag. The tag is set using {@link com.crossbow.volley.FileRequest#setTag(Object) FileRequest.setTag}
     * @param tag to match against
     */
    public void cancelAll(Object tag) {

        if(tag == null) {
            throw new IllegalArgumentException("Cant cancel for a null tag");
        }

        synchronized (inFlightRequests) {
            for(FileRequest<?> request : inFlightRequests) {
                if(tag.equals(request.getTag())) {
                    request.cancel();
                }
            }
        }
    }

    protected void finishRequest(FileRequest fileRequest) {
        synchronized (inFlightRequests) {
            inFlightRequests.remove(fileRequest);
        }

        synchronized (inUseFiles) {
            String filePath = fileRequest.getFilePath();
            inUseFiles.remove(filePath);
            //get any blocked requests nad add to the queue
            if(blockedRequests.containsKey(filePath)) {
                LinkedList<FileRequest<?>> blocked = blockedRequests.get(filePath);
                FileRequest<?> request = blocked.pop();
                requestQueue.add(request);
                inUseFiles.add(filePath);

                fileRequest.mark("request-unblocked");

                if(blocked.isEmpty()) {
                    blockedRequests.remove(filePath);
                }
            }
        }
    }
}
