package com.crossbow.volley;

import android.test.suitebuilder.annotation.SmallTest;

import com.android.volley.VolleyError;
import com.crossbow.volley.toolbox.Files;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**

 */
public class FilesTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File file;

    @Before
    public void setUp() throws Exception {
        file = temporaryFolder.newFile();
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write("testfilecontents".getBytes());
        outputStream.flush();
        outputStream.close();
    }

    @Test
    public void testInputStreamStringRead() throws Exception {
        String data = Files.readInputStreamString(new FileInputStream(file));
        Assert.assertEquals("testfilecontents", data);
    }

    @Test
    public void testInputStreamCopy() throws VolleyError, IOException {
        File testCopy = temporaryFolder.newFile();
        Files.copyFileFromStream(new FileInputStream(file), testCopy);
        Assert.assertTrue(testCopy.exists());
        Assert.assertTrue(Arrays.equals("testfilecontents".getBytes(), Files.readFileData(testCopy)));
        Assert.assertEquals("testfilecontents", Files.readFileString(testCopy));
    }

    @Test
    public void testFileWrite() throws VolleyError, IOException {
        File testCopy = temporaryFolder.newFile();
        Files.writeFileString(testCopy, "test-file write");
        Assert.assertEquals("test-file write", Files.readFileString(testCopy));
    }

    @Test
    public void testFileWriteData() throws VolleyError, IOException {
        File testCopy = temporaryFolder.newFile();
        Files.writeFileData(testCopy, "test-file write".getBytes());
        Assert.assertEquals("test-file write", Files.readFileString(testCopy));
    }

    @Test
    public void testFileWriteLines() throws VolleyError, IOException {
        File testCopy = temporaryFolder.newFile();
        String[] lines = new String[]{"line1", "line2", "line3"};
        Files.writeFileLines(testCopy, lines);

        String[] readLines = new String[3];
        Files.readFileLines(testCopy).toArray(readLines);

        Assert.assertTrue(Arrays.equals(lines, readLines));
    }
}
