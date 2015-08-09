package com.crossbow.volley;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.volley.VolleyError;
import com.crossbow.BuildConfig;
import com.crossbow.volley.mock.MockFileRequest;
import com.crossbow.volley.mock.MockFileStack;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import static junit.framework.Assert.*;

/**

 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class FileQueueTest {

    FileQueue fileQueue;
    CountDownLatch countDownLatch;

    @Before
    public void setUp() throws Exception {
        MockFileStack mockFileStack = new MockFileStack();
        MockFileDelivery mockFileDelivery = new MockFileDelivery(true);

        fileQueue = new FileQueue(mockFileDelivery, mockFileStack);
    }

    @Test
    public void testFileReadRequest() throws InterruptedException {
        MockFileRequest fileRequest = new MockFileRequest("tests/res/test-file.txt", new FileResponse.Listener<String>() {
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

    @Test
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

    @Test
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

    @After
    public void tearDown() throws Exception {
        fileQueue.stop();
    }

    private FileResponse.ErrorListener errorListener = new FileResponse.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            countDownLatch.countDown();
        }
    };
}
