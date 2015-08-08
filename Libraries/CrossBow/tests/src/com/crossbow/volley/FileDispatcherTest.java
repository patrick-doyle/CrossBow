package com.crossbow.volley;

import android.test.suitebuilder.annotation.LargeTest;

import com.android.volley.VolleyError;
import com.crossbow.volley.*;
import com.crossbow.volley.mock.MockFileRequest;
import com.crossbow.volley.mock.MockFileStack;

import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;

/**

 */
public class FileDispatcherTest extends TestCase {

    private FileDispatcher dispatcher;
    private PriorityBlockingQueue<FileRequest<?>> queue = new PriorityBlockingQueue<>();
    private MockFileStack mockFileReader = new MockFileStack();
    private com.crossbow.volley.MockFileDelivery mockFileDelivery = new com.crossbow.volley.MockFileDelivery();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dispatcher = new FileDispatcher(queue, mockFileReader, mockFileDelivery);
    }

    @LargeTest
    public void testSuccessDispatcher() throws InterruptedException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final boolean[] sucess = new boolean[1];
        final boolean[] error = new boolean[1];

        MockFileRequest fileRequest = new MockFileRequest(new FileResponse.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sucess[0] = true;
                countDownLatch.countDown();
            }
        }, new FileResponse.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                error[0] = true;
                countDownLatch.countDown();
            }
        });
        queue.add(fileRequest);
        dispatcher.start();
        countDownLatch.await();

        assertTrue(sucess[0]);
        assertFalse(error[0]);
    }

    @LargeTest
    public void testErrorDispatcher() throws InterruptedException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final boolean[] sucess = new boolean[1];
        final boolean[] error = new boolean[1];

        mockFileReader.setShouldFail(true);

        MockFileRequest fileRequest = new MockFileRequest(new FileResponse.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sucess[0] = true;
                countDownLatch.countDown();
            }
        }, new FileResponse.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                error[0] = true;
                countDownLatch.countDown();
            }
        });
        queue.add(fileRequest);
        dispatcher.start();

        countDownLatch.await();

        assertFalse(sucess[0]);
        assertTrue(error[0]);

        mockFileReader.setShouldFail(false);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dispatcher.quit();
        dispatcher.join();
    }
}
