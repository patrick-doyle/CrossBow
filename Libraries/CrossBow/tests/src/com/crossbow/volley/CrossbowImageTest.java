package com.crossbow.volley;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.test.AndroidTestCase;
import android.widget.ImageView;

import com.crossbow.volley.CrossbowImage;
import com.crossbow.volley.ScaleTypeDrawable;

/**

 */
public class CrossbowImageTest extends AndroidTestCase {

    private ImageView imageView;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        imageView = new ImageView(getContext());
    }

    public void testImageBitmapCenterCrop() {
        CrossbowImage.Builder builder = new CrossbowImage.Builder(getContext(), null, null);
        builder.scale(ImageView.ScaleType.CENTER);
        CrossbowImage crossbowImage = builder.into(imageView).load();

        //Same method call as if the image loads
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ALPHA_8);
        crossbowImage.setBitmap(bitmap, false);

        ScaleTypeDrawable drawable = (ScaleTypeDrawable) imageView.getDrawable();
        assertTrue(drawable.getScaleType() == ImageView.ScaleType.CENTER);
    }

    public void testImageFadeDrawable() {
        ColorDrawable defaultDrawable = new ColorDrawable(Color.BLUE);

        CrossbowImage.Builder builder = new CrossbowImage.Builder(getContext(), null, null);
        builder.placeholder(defaultDrawable);
        builder.scale(ImageView.ScaleType.CENTER);
        builder.fade(200);
        CrossbowImage crossbowImage = builder.into(imageView).load();

        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ALPHA_8);
        crossbowImage.setBitmap(bitmap, false);

        TransitionDrawable drawable = (TransitionDrawable) imageView.getDrawable();
        assertTrue(drawable.getNumberOfLayers() == 2);
    }

    public void testErrorDrawable() {
        ColorDrawable errorDrawable = new ColorDrawable(Color.BLUE);
        CrossbowImage.Builder builder = new CrossbowImage.Builder(getContext(), null, null);
        builder.errorScale(ImageView.ScaleType.FIT_END);
        builder.error(errorDrawable);
        CrossbowImage crossbowImage = builder.into(imageView).load();

        //Same method call as if the image fails
        crossbowImage.setError(null);

        ScaleTypeDrawable drawable = (ScaleTypeDrawable) imageView.getDrawable();
        assertEquals(drawable.getScaleType() , ImageView.ScaleType.FIT_END);
        assertEquals(drawable.getSourceDrawable() , errorDrawable);
    }

    public void testDefaultErrorDrawable() {
        ColorDrawable defaultDrawable = new ColorDrawable(Color.BLUE);
        CrossbowImage.Builder builder = new CrossbowImage.Builder(getContext(), null, null);
        builder.placeholder(defaultDrawable);
        builder.placeholderScale(ImageView.ScaleType.FIT_END);
        CrossbowImage crossbowImage = builder.into(imageView).load();

        crossbowImage.setError(null);

        ScaleTypeDrawable drawable = (ScaleTypeDrawable) imageView.getDrawable();
        assertEquals(drawable.getScaleType() , ImageView.ScaleType.FIT_END);
        assertEquals(drawable.getSourceDrawable() , defaultDrawable);
    }

    public void testErrorDefaultFade() {
        ColorDrawable defaultDrawable = new ColorDrawable(Color.BLUE);
        ColorDrawable errorDrawable = new ColorDrawable(Color.BLACK);

        CrossbowImage.Builder builder = new CrossbowImage.Builder(getContext(), null, null);
        builder.placeholder(defaultDrawable);
        builder.error(errorDrawable);
        builder.fade(200);
        CrossbowImage crossbowImage = builder.into(imageView).load();

        crossbowImage.setError(null);

        TransitionDrawable drawable = (TransitionDrawable) imageView.getDrawable();
        assertTrue(drawable.getNumberOfLayers() == 2);
    }
}
