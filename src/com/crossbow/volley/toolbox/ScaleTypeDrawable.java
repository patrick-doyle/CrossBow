package com.crossbow.volley.toolbox;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.ImageView;

/**
 * Created by Patrick on 13/04/2015.
 */
public class ScaleTypeDrawable extends Drawable {

    private static final int WIDTH = 1;
    private static final int HEIGHT = -1;

    private Drawable sourceDrawable;

    private ImageView.ScaleType scaleType;

    private Rect bounds;

    private Rect insetBounds = new Rect();

    public ScaleTypeDrawable(Drawable sourceDrawable, ImageView.ScaleType scaleType) {
        this.sourceDrawable = sourceDrawable;
        this.scaleType = scaleType;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        sourceDrawable.setBounds(insetBounds);
        sourceDrawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        sourceDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        sourceDrawable.setColorFilter(cf);
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        this.bounds = bounds;
        calcInsetBounds();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        bounds = new Rect(left, top, right, bottom);
        calcInsetBounds();
    }

    @Override
    public void setColorFilter(int color, @NonNull PorterDuff.Mode mode) {
        sourceDrawable.setColorFilter(color, mode);
    }

    @Override
    public int getOpacity() {
        return sourceDrawable.getOpacity();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setHotspot(float x, float y) {
        sourceDrawable.setHotspot(x, y);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setHotspotBounds(int left, int top, int right, int bottom) {
        sourceDrawable.setHotspotBounds(left, top, right, bottom);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void setAutoMirrored(boolean mirrored) {
        sourceDrawable.setAutoMirrored(mirrored);
    }

    public void calcInsetBounds() {

        int width = bounds.width();
        int height = bounds.height();

        int sourceWidth = sourceDrawable.getIntrinsicWidth();
        int sourceHeight = sourceDrawable.getIntrinsicHeight();

        //calc the new bounds based on source type
        switch (scaleType) {

            case CENTER_CROP:
                insetBounds = fitOver(sourceWidth, sourceHeight, width, height);
                break;

            case FIT_CENTER:
            case CENTER_INSIDE:
                insetBounds = fitInto(sourceWidth, sourceHeight, width, height);
                Gravity.apply(Gravity.CENTER, insetBounds.width(), insetBounds.height(), bounds, insetBounds);
                break;

            case CENTER:
                Gravity.apply(Gravity.CENTER, sourceWidth, sourceHeight, bounds, insetBounds);
                break;

            case FIT_XY:
                insetBounds = new Rect(bounds);
                break;

            case FIT_END:
                insetBounds = fitInto(sourceWidth, sourceHeight, width, height);
                int right = bounds.right - insetBounds.width();
                insetBounds.offsetTo(right, 0);
                break;

            case FIT_START:
                insetBounds = fitInto(sourceWidth, sourceHeight, width, height);
                insetBounds.offsetTo(0, 0);
                break;
        }
    }

    /**
     * Scales the width and height into the max width and height while keeping the aspect ratio. Similar to ImageView Scaletype fitCenter.
     *
     * @param width width to scale
     * @param height height to scale
     * @param maxWidth the outerBounds width
     * @param maxHeight outerBounds height
     * @return Rect starting a 0,0 and matching the outerBounds.
     */
    private static Rect fitInto(float width, float height, float maxWidth, float maxHeight) {

        Rect rect = new Rect();

        float inputRatio = width/height;
        float outputRatio = maxWidth/maxHeight;

        float outWidth;
        float outHeight;
        if(inputRatio > outputRatio) {
            outWidth = maxWidth;
            outHeight = outWidth / inputRatio;
        }
        else {
            outHeight = maxHeight;
            outWidth = maxHeight * inputRatio;
        }

        rect.right = (int) outWidth;
        rect.bottom = (int) outHeight;

        return rect;
    }

    /**
     * Scales the width and height over the max width and height while keeping the aspect ratio. Similar to ImageView Scaletype centerCrop.
     *
     * @param width width to scale
     * @param height height to scale
     * @param maxWidth the outerBounds width
     * @param maxHeight outerBounds height
     * @return Rect starting a 0,0 and matching the outerBounds.
     */
    private static Rect fitOver(float width, float height, float maxWidth, float maxHeight) {

        Rect rect = new Rect();

        float inputRatio = width/height;
        float outputRatio = maxWidth/maxHeight;

        float outWidth;
        float outHeight;
        if(inputRatio < outputRatio) {
            outWidth = maxWidth;
            outHeight = outWidth / inputRatio;
        }
        else {
            outHeight = maxHeight;
            outWidth = maxHeight * inputRatio;
        }

        rect.right = (int) outWidth;
        rect.bottom = (int) outHeight;

        return rect;
    }
}
