package com.crossbow.volley.toolbox;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.mock.ShadowSystemClock;
import com.crossbow.BuildConfig;
import com.crossbow.volley.FileError;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**

 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowSystemClock.class})
public class ImageDecoderTest  {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testImageDecoderDownscaleAspectRatio() throws Exception {
        int height = ImageDecoder.getResizedDimension(500, 500, 720, 1280, ImageView.ScaleType.CENTER_CROP);
        int width = ImageDecoder.getResizedDimension(500, 500, 1280, 720, ImageView.ScaleType.CENTER_CROP);
        assertEquals(500, height);
        assertEquals(888, width);
    }

    @Test
    public void testImageDecoderUpscaleAspectRatio() throws Exception {
        int height = ImageDecoder.getResizedDimension(1080, 1920, 720, 1280, ImageView.ScaleType.CENTER_CROP);
        int width = ImageDecoder.getResizedDimension(1920, 1080, 1280, 720, ImageView.ScaleType.CENTER_CROP);
        assertEquals(1920, width);
        assertEquals(1080, height);
    }

    @Test
    public void testImageDecoderConfig() throws Exception {
        byte[] data = readImageFromAssets();

        Bitmap bitmap = ImageDecoder.parseImage(data, Bitmap.Config.ARGB_8888, ImageView.ScaleType.CENTER_CROP, 500, 500);
        assertEquals(Bitmap.Config.ARGB_8888, bitmap.getConfig());
    }

    @Test
    public void testImageDecoderFileConfig() throws Exception {
        File data = copyToFile();

        Bitmap bitmap = ImageDecoder.parseFile(data, Bitmap.Config.ARGB_8888, ImageView.ScaleType.CENTER, 500, 500);
        assertEquals(Bitmap.Config.ARGB_8888, bitmap.getConfig());
    }

    private byte[] readImageFromAssets() throws Exception {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            String filePath = RuntimeEnvironment.application.getPackageResourcePath() + "/tests/res/test-image.jpg";
            InputStream inputStream = new FileInputStream(new File(filePath));

            int size = 0;

            byte[] buffer = new byte[2048];
            while ((size = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, size);
            }

            inputStream.close();
            byte[] data = outputStream.toByteArray();
            outputStream.close();
            return data;
        } catch (IOException e) {
            throw new FileError(e);
        }
    }

    private File copyToFile() throws Exception {

        File file = folder.newFile();

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            String filePath = RuntimeEnvironment.application.getPackageResourcePath() + "/tests/res/test-image.jpg";
            InputStream inputStream = new FileInputStream(new File(filePath));

            int size = 0;

            byte[] buffer = new byte[2048];
            while((size = inputStream.read(buffer)) >= 0){
                outputStream.write(buffer,0,size);
            }

            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            throw new FileError(e);
        }

        return file;
    }
}
