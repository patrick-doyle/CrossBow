package com.crossbow.volley;

import android.test.suitebuilder.annotation.SmallTest;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import junit.framework.TestCase;

import java.io.File;

/**
 * Created by Patrick on 26/03/2015.
 */
public class FileRequestTest extends TestCase {

    @SmallTest
    public void testFileRequestPriority() {
        int sequence = 0;

        TestRequest low = new TestRequest(Request.Priority.LOW);
        low.sequence(sequence++);
        TestRequest low2 = new TestRequest(Request.Priority.LOW);
        low2.sequence(sequence++);
        TestRequest normal = new TestRequest(Request.Priority.NORMAL);
        normal.sequence(sequence++);
        TestRequest high = new TestRequest(Request.Priority.HIGH);
        high.sequence(sequence++);
        TestRequest immeditate = new TestRequest(Request.Priority.IMMEDIATE);


        assertTrue(low.compareTo(normal) > 0);
        assertTrue(low.compareTo(high) > 0);
        assertTrue(low2.compareTo(low) > 0);
        assertTrue(immeditate.compareTo(high) < 0);
        assertTrue(high.compareTo(high) == 0);
    }

    private static class TestRequest extends FileRequest {

        private Request.Priority priority;
        private static int count = 9;

        public TestRequest(Request.Priority priority) {
            super(Integer.toHexString(count++), null);
            this.priority = priority;
        }

        @Override
        protected void deliverResult(Object result) {

        }

        @Override
        public Request.Priority getPriority() {
            return priority;
        }

        @Override
        public FileResponse doFileWork(File file) throws VolleyError {
            return null;
        }
    }
}
