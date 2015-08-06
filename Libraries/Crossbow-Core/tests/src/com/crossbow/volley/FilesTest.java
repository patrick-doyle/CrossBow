package com.crossbow.volley;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.volley.VolleyError;
import com.crossbow.volley.toolbox.Files;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**

 */
public class FilesTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testFileAssetStringRead() throws VolleyError {
        String data = Files.readAssetString(getContext(), "test-file.txt");
        assertEquals("testfilecontents", data);
    }

    @SmallTest
    public void testFileAssetRead() throws VolleyError {
        byte[] data = Files.readAssetData(getContext(), "test-file.txt");
        assertTrue(Arrays.equals("testfilecontents".getBytes(), data));
    }

    @SmallTest
    public void testFileAssetCopy() throws VolleyError {
        File testCopy = new File(getContext().getFilesDir() + File.separator + "test-dir");
        testCopy.mkdirs();
        testCopy = new File(testCopy, "files-test.test");
        Files.copyFileFromAssets(getContext(), "test-file.txt", testCopy);
        assertTrue(testCopy.exists());
        assertTrue(Arrays.equals("testfilecontents".getBytes(),  Files.readFileData(testCopy)));
        assertEquals("testfilecontents", Files.readFileString(testCopy));
    }

    @SmallTest
    public void testFileWrite() throws VolleyError {
        File testCopy = new File(getContext().getFilesDir() + File.separator + "test-dir");
        testCopy.mkdirs();
        testCopy = new File(testCopy, "files-write1.test");
        Files.writeFileString(testCopy, "test-file write");
        assertEquals("test-file write", Files.readFileString(testCopy));
    }

    @SmallTest
    public void testFileWriteData() throws VolleyError {
        File testCopy = new File(getContext().getFilesDir() + File.separator + "test-dir");
        testCopy.mkdirs();
        testCopy = new File(testCopy, "files-write-data.test");
        Files.writeFileData(testCopy, "test-file write".getBytes());
        assertEquals("test-file write", Files.readFileString(testCopy));
    }

    @SmallTest
    public void testFileWriteLines() throws VolleyError {
        File testCopy = new File(getContext().getFilesDir() + File.separator + "test-dir");
        testCopy.mkdirs();
        testCopy = new File(testCopy, "files-write-lines.test");
        String[] lines = new String[]{"line1", "line2", "line3"};
        Files.writeFileLines(testCopy, lines);

        String[] readLines = new String[3];
        Files.readFileLines(testCopy).toArray(readLines);

        assertTrue(Arrays.equals(lines, readLines));
    }
}
