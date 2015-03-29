package com.crossbow.volley;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.android.volley.VolleyError;
import com.crossbow.volley.toolbox.BasicFileReader;
import com.crossbow.volley.toolbox.StringFileRequest;

/**
 * Created by Patrick on 26/03/2015.
 */
public class BasicFileReaderTest extends AndroidTestCase {

    @MediumTest
    public void testFileReader() throws VolleyError {

        FileRequest request = new StringFileRequest("testFile.txt", null, null);

        FileReader fileReader = new BasicFileReader(getContext());
        byte[] fileData = fileReader.readFile(request);
        assertEquals("testfilecontents", new String(fileData));
    }

}
