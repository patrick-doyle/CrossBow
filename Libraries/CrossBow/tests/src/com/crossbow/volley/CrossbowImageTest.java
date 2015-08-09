package com.crossbow.volley;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import com.crossbow.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSystemClock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**

 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowSystemClock.class})
public class CrossbowImageTest {

    private ImageView imageView;

    @Before
    public void setUp() throws Exception {
        imageView = new ImageView(RuntimeEnvironment.application);
    }

    @Test
    public void testImageBitmapCenterCrop() {
        CrossbowImage.Builder builder = new CrossbowImage.Builder(RuntimeEnvironment.application, null, null);
        builder.scale(ImageView.ScaleType.CENTER);
        CrossbowImage crossbowImage = builder.into(imageView).load();

        //Same method call as if the image loads
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ALPHA_8);
        crossbowImage.setBitmap(bitmap, false);

        ScaleTypeDrawable drawable = (ScaleTypeDrawable) imageView.getDrawable();
        assertTrue(drawable.getScaleType() == ImageView.ScaleType.CENTER);
    }

    @Test
    public void testImageFadeDrawable() {
        ColorDrawable defaultDrawable = new ColorDrawable(Color.BLUE);

        CrossbowImage.Builder builder = new CrossbowImage.Builder(RuntimeEnvironment.application, null, null);
        builder.placeholder(defaultDrawable);
        builder.scale(ImageView.ScaleType.CENTER);
        builder.fade(200);
        CrossbowImage crossbowImage = builder.into(imageView).load();

        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ALPHA_8);
        crossbowImage.setBitmap(bitmap, false);

        TransitionDrawable drawable = (TransitionDrawable) imageView.getDrawable();
        assertTrue(drawable.getNumberOfLayers() == 2);
    }

    @Test
    public void testErrorDrawable() {
        ColorDrawable errorDrawable = new ColorDrawable(Color.BLUE);
        CrossbowImage.Builder builder = new CrossbowImage.Builder(RuntimeEnvironment.application, null, null);
        builder.errorScale(ImageView.ScaleType.FIT_END);
        builder.error(errorDrawable);
        CrossbowImage crossbowImage = builder.into(imageView).load();

        //Same method call as if the image fails
        crossbowImage.setError(null);

        ScaleTypeDrawable drawable = (ScaleTypeDrawable) imageView.getDrawable();
        assertEquals(drawable.getScaleType() , ImageView.ScaleType.FIT_END);
        assertEquals(drawable.getSourceDrawable() , errorDrawable);
    }

    @Test
    public void testDefaultErrorDrawable() {
        ColorDrawable defaultDrawable = new ColorDrawable(Color.BLUE);
        CrossbowImage.Builder builder = new CrossbowImage.Builder(RuntimeEnvironment.application, null, null);
        builder.placeholder(defaultDrawable);
        builder.placeholderScale(ImageView.ScaleType.FIT_END);
        CrossbowImage crossbowImage = builder.into(imageView).load();

        crossbowImage.setError(null);

        ScaleTypeDrawable drawable = (ScaleTypeDrawable) imageView.getDrawable();
        assertEquals(drawable.getScaleType() , ImageView.ScaleType.FIT_END);
        assertEquals(drawable.getSourceDrawable() , defaultDrawable);
    }

    @Test
    public void testErrorDefaultFade() {
        ColorDrawable defaultDrawable = new ColorDrawable(Color.BLUE);
        ColorDrawable errorDrawable = new ColorDrawable(Color.BLACK);

        CrossbowImage.Builder builder = new CrossbowImage.Builder(RuntimeEnvironment.application, null, null);
        builder.placeholder(defaultDrawable);
        builder.error(errorDrawable);
        builder.fade(200);
        CrossbowImage crossbowImage = builder.into(imageView).load();

        crossbowImage.setError(null);

        TransitionDrawable drawable = (TransitionDrawable) imageView.getDrawable();
        assertTrue(drawable.getNumberOfLayers() == 2);
    }
}
