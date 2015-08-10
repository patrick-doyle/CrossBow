package com.crossbow.wear.receiver;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.ParseError;
import com.crossbow.volley.toolbox.ImageDecoder;
import com.crossbow.wear.core.ParamsBundle;
import com.crossbow.wear.core.ResponseTransformer;

import java.io.ByteArrayOutputStream;

/**
 * Used to compress the images to a smaller size before they get sent to the wearable
 */
public class ImageRequestTransformer implements ResponseTransformer {

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public byte[] transform(ParamsBundle requestArgs, byte[] data) throws ParseError {

        int width = requestArgs.getInt("width", 500);
        int height = requestArgs.getInt("height", 500);
        Bitmap.Config config = (Bitmap.Config) requestArgs.getSerializable("config");
        ImageView.ScaleType scaleType = (ImageView.ScaleType) requestArgs.getSerializable("scale_type");
        if(config == null) {
            config = Bitmap.Config.RGB_565;
        }

        if(scaleType == null) {
            scaleType = ImageView.ScaleType.CENTER_INSIDE;
        }

        Bitmap bitmap = ImageDecoder.parseImage(data, config, scaleType, width, height);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream);
        return stream.toByteArray();
    }
}
