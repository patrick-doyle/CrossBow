package com.crossbow.volley;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.volley.VolleyError;
import com.crossbow.volley.FileQueue;
import com.crossbow.volley.FileRequest;
import com.crossbow.volley.FileResponse;
import com.crossbow.volley.MockFileDelivery;
import com.crossbow.volley.mock.MockFileRequest;
import com.crossbow.volley.mock.MockFileStack;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**

 */
public class FileQueueTest extends AndroidTestCase {

    FileQueue fileQueue;
    CountDownLatch countDownLatch;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockFileStack mockFileStack = new MockFileStack();
        MockFileDelivery mockFileDelivery = new MockFileDelivery(true);

        fileQueue = new FileQueue(mockFileDelivery, mockFileStack);
    }

    @SmallTest
    public void testFileReadRequest() throws InterruptedException {
        MockFileRequest fileRequest = new MockFileRequest("test-file.txt", new FileResponse.Listener<String>() {
            @Override
            public void onResponse(String response) {
                countDownLatch.countDown();
                assertEquals("mockdatarequest", response);
            }
        }, errorListener);
        countDownLatch = new CountDownLatch(1);
        fileQueue.start();
        fileQueue.add(fileRequest);
        countDownLatch.await();
    }

    @SmallTest
    public void testFileRequestCancel() throws InterruptedException {
        MockFileRequest request1 = new MockFileRequest("123", null, null);
        request1.setTag("test_tag");

        MockFileRequest request2 = new MockFileRequest("456", null,null);
        request2.setTag("test_tag_2");

        MockFileRequest request3 = new MockFileRequest("789",null, null);
        request3.setTag("test_tag");

        fileQueue.add(request1);
        fileQueue.add(request2);
        fileQueue.add(request3);
        fileQueue.cancelAll("test_tag");

        assertTrue(request1.isCanceled());
        assertFalse(request2.isCanceled());
        assertTrue(request3.isCanceled());
    }

    @SmallTest
    public void testFileBlocking() {
        MockFileRequest request1 = new MockFileRequest("123", null, null);
        request1.setTag("test_tag");

        MockFileRequest request2 = new MockFileRequest("123", null, null);
        request2.setTag("test_tag_2");

        MockFileRequest request3 = new MockFileRequest("123",null, null);
        request3.setTag("test_tag");

        fileQueue.add(request1);
        fileQueue.add(request2);
        fileQueue.add(request3);

        Collection<FileRequest<?>> requestQueue = fileQueue.getRequestQueue();
        assertTrue(requestQueue.contains(request1));
        assertFalse(requestQueue.contains(request2));
        assertFalse(requestQueue.contains(request3));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        fileQueue.stop();
    }

    private FileResponse.ErrorListener errorListener = new FileResponse.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            countDownLatch.countDown();
        }
    };
}
