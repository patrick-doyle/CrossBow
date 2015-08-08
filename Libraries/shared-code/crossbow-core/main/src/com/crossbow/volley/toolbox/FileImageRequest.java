package com.crossbow.volley.toolbox;

import android.graphics.Bitmap;

import com.android.volley.ParseError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.crossbow.volley.FileRequest;
import com.crossbow.volley.FileResponse;

import java.io.File;

/**

 */
public class FileImageRequest extends FileRequest<Bitmap> {

    private final Bitmap.Config config;
    private final int maxWidth;
    private final int maxHeight;
    private final FileResponse.Listener<Bitmap> listener;

    public FileImageRequest(String filePath, Bitmap.Config config, int maxWidth, int maxHeight, FileResponse.ErrorListener errorListener, FileResponse.Listener<Bitmap> listener) {
        super(filePath, errorListener);
        this.config = config;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.listener = listener;
    }

    @Override
    public FileResponse<Bitmap> doFileWork(File file) throws VolleyError {
        try {
            if (isCanceled()) {
                return null;
            }
;
            Bitmap parsed = ImageDecoder.parseFile(file, config, maxWidth, maxHeight);

            if (parsed != null) {
                return FileResponse.success(parsed);
            }
            else {
                return FileResponse.error();
            }

        }
        catch (OutOfMemoryError e) {
            VolleyLog.e("Caught OOM for file image, path=%s", getFilePath());
            return FileResponse.error(e);
        } catch (ParseError parseError) {
            return FileResponse.error(parseError);
        }
    }

    @Override
    protected void deliverResult(Bitmap result) {
        listener.onResponse(result);
    }
}
