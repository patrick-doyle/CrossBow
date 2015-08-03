package com.crossbow.volley;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.crossbow.volley.toolbox.Files;
import com.crossbow.volley.toolbox.ImageDecoder;

import java.io.File;

/**

 */
public class ImageDecoderTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testImageDecoderDownscale() throws Exception {
        byte[] data = readImageFromAssets();

        Bitmap bitmap = ImageDecoder.parseImage(data, 500, 500);
        assertEquals(500, bitmap.getWidth());
        assertEquals(281, bitmap.getHeight());
    }

    @SmallTest
    public void testImageDecoderUpscale() throws Exception {
        byte[] data = readImageFromAssets();

        Bitmap bitmap = ImageDecoder.parseImage(data, 1920, 1080);
        assertEquals(1280, bitmap.getWidth());
        assertEquals(720, bitmap.getHeight());
    }

    @SmallTest
    public void testImageDecoderConfig() throws Exception {
        byte[] data = readImageFromAssets();

        Bitmap bitmap = ImageDecoder.parseImage(data, Bitmap.Config.ARGB_8888,  500, 500);
        assertEquals(Bitmap.Config.ARGB_8888, bitmap.getConfig());
    }

    @SmallTest
    public void testImageDecoderFileUpscale() throws Exception {
        File data = copyToFile();

        Bitmap bitmap = ImageDecoder.parseFile(data, 1920, 1080);
        assertEquals(1280, bitmap.getWidth());
        assertEquals(720, bitmap.getHeight());
    }

    @SmallTest
    public void testImageDecoderFileDownscale() throws Exception {
        File data = copyToFile();
        Bitmap bitmap = ImageDecoder.parseFile(data, 500, 500);
        assertEquals(500, bitmap.getWidth());
        assertEquals(281, bitmap.getHeight());
    }

    @SmallTest
    public void testImageDecoderFileConfig() throws Exception {
        File data = copyToFile();

        Bitmap bitmap = ImageDecoder.parseFile(data, Bitmap.Config.ARGB_8888, 500, 500);
        assertEquals(Bitmap.Config.ARGB_8888, bitmap.getConfig());
    }

    private byte[] readImageFromAssets() throws Exception {
        return Files.readAssetData(getContext(), "test-image.jpg");
    }

    private File copyToFile() throws Exception {
        File path = new File(getContext().getFilesDir()+ "/testDir", "test-image.jpg");
        if(path.exists()) {
            path.delete();
        }
        else {
            path.getParentFile().mkdirs();
        }
        Files.copyFileFromAssets(getContext(), "test-image.jpg", path);
        return path;
    }
}
