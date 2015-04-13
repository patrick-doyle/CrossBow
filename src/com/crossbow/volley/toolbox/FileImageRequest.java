package com.crossbow.volley.toolbox;

import android.graphics.Bitmap;

import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.crossbow.volley.FileReadStreamRequest;
import com.crossbow.volley.FileResponse;

import java.io.InputStream;

/**
 * Created by Patrick on 12/04/2015.
 */
public class FileImageRequest extends FileReadStreamRequest<Bitmap> {

    private final Bitmap.Config config;
    private final int maxWidth;
    private final int maxHeight;
    private final FileResponse.ReadListener<Bitmap> listener;

    public FileImageRequest(String filePath, Bitmap.Config config, int maxWidth, int maxHeight, FileResponse.ErrorListener errorListener, FileResponse.ReadListener<Bitmap> listener) {
        super(filePath, errorListener);
        this.config = config;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.listener = listener;
    }

    @Override
    protected Bitmap parseData(InputStream fileData) {
        try {
            if (isCanceled()) {
                return null;
            }
            Bitmap parsed = ImageDecoder.parseStream(fileData, config, maxWidth, maxHeight);

            if (parsed != null) {
                return parsed;
            }
            else {
                return null;
            }

        }
        catch (OutOfMemoryError e) {
            VolleyLog.e("Caught OOM for file image, path=%s", getFilePath());
            return null;
        } catch (ParseError parseError) {
            return null;
        }
    }

    @Override
    protected void deliverResult(Bitmap result) {
        listener.onResponse(result);
    }
}
