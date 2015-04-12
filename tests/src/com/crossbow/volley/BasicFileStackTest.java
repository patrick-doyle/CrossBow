package com.crossbow.volley;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.android.volley.VolleyError;
import com.crossbow.volley.toolbox.BasicFileStack;
import com.crossbow.volley.toolbox.StringFileRequest;

/**
 * Created by Patrick on 26/03/2015.
 */
public class BasicFileStackTest extends AndroidTestCase {

    @MediumTest
    public void testFileReader() throws VolleyError {

        FileRequest request = new StringFileRequest("testFile.txt", null, null);

        FileStack fileStack = new BasicFileStack(getContext());
        byte[] fileData = fileStack.readFileData(request);
        assertEquals("testfilecontents", new String(fileData));
    }

}
