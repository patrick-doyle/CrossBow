package com.crossbow.volley;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.ImageView;

/**
 * Created by Patrick on 13/04/2015.
 *
 * Drawable wrapper that allows a ImageView.ScaleType to be applied to the
 */
public class ScaleTypeDrawable extends Drawable {

    private final Drawable sourceDrawable;

    private final ImageView.ScaleType scaleType;

    private Rect bounds = new Rect();

    private Rect insetBounds = new Rect();

    private static Rect debugBounds = new Rect();

    private final boolean fromCache;
    private static final Paint CACHE_PAINT = new Paint();

    static {
        CACHE_PAINT.setColor(Color.GREEN);
    }

    ScaleTypeDrawable(Drawable sourceDrawable, ImageView.ScaleType scaleType, boolean fromCache) {
        this.sourceDrawable = sourceDrawable;
        this.scaleType = scaleType;
        this.fromCache = fromCache;
        debugBounds.left = 0;
        debugBounds.top = 0;
    }

    public ScaleTypeDrawable(Drawable sourceDrawable, ImageView.ScaleType scaleType) {
        this(sourceDrawable, scaleType, false);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        sourceDrawable.setBounds(insetBounds);
        sourceDrawable.draw(canvas);
        if(fromCache) {
            canvas.drawRect(debugBounds, CACHE_PAINT);
        }
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
        if(fromCache) {
            debugBounds.right = bounds.width() / 15;
            debugBounds.bottom = debugBounds.right;
        }
        sourceDrawable.setBounds(bounds);
        calcInsetBounds();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        bounds.left = left;
        bounds.top = top;
        bounds.right = right;
        bounds.bottom = bottom;
        if(fromCache) {
            debugBounds.right = bounds.width() / 15;
            debugBounds.bottom = debugBounds.right;
        }
        calcInsetBounds();
        sourceDrawable.setBounds(bounds);
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

    private void calcInsetBounds() {

        int width = bounds.width();
        int height = bounds.height();

        //Handles the case for color drawables and drawables without Intrinsic size. These do not need to be scaled as they can fill the ImageView
        if(sourceDrawable.getIntrinsicWidth() < 0 && sourceDrawable.getIntrinsicHeight() < 0) {
            insetBounds = new Rect(bounds);
            return;
        }
        int sourceWidth = sourceDrawable.getIntrinsicWidth() != 0 ? sourceDrawable.getIntrinsicWidth() : 1;
        int sourceHeight = sourceDrawable.getIntrinsicHeight() != 0 ? sourceDrawable.getIntrinsicHeight() : 1;

        //calc the new bounds based on source type
        switch (scaleType) {

            case CENTER_CROP:
                insetBounds = fitOver(sourceWidth, sourceHeight, width, height);
                Gravity.apply(Gravity.CENTER, insetBounds.width(), insetBounds.height(), bounds, insetBounds);
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

    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    public Drawable getSourceDrawable() {
        return sourceDrawable;
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
     * @return Rect starting at 0,0 and matching the outerBounds.
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

    public static Drawable wrapDrawable(Drawable drawable, ImageView.ScaleType scaleType) {
        return new ScaleTypeDrawable(drawable, scaleType);
    }

    public static Drawable wrapDrawable(Context context, @DrawableRes int drawableRes, ImageView.ScaleType scaleType) {
        return new ScaleTypeDrawable(context.getResources().getDrawable(drawableRes), scaleType);
    }
}
