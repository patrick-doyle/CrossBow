package com.crossbow.volley.toolbox;

import android.graphics.Bitmap;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.FileQueue;
import com.crossbow.volley.FileResponse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Patrick on 13/04/2015.
 */
public class FileImageLoader {

    private FileQueue fileQueue;

    private Map<String, BatchedFileRequests> batchedFileRequests = new HashMap<>();

    private ImageLoader.ImageCache imageCache;

    public FileImageLoader(FileQueue fileQueue, ImageLoader.ImageCache imageCache) {
        this.fileQueue = fileQueue;
        this.imageCache = imageCache;
    }

    public FileImageContainer get(String filePath, int maxWidth, int maxHeight, Listener listener) {

        final String cacheKey = getCacheKey(filePath, maxWidth, maxHeight);

        FileImageContainer fileImageContainer = new FileImageContainer(cacheKey, imageCache);
        fileImageContainer.listener = listener;

        Bitmap bitmap = imageCache.getBitmap(cacheKey);
        if(bitmap != null) {
            fileImageContainer.callListener(bitmap, true);
        }
        else {

            //check if there is a
            if(batchedFileRequests.containsKey(cacheKey)) {
                batchedFileRequests.get(cacheKey).addConatiner(fileImageContainer);
            }
            else {

                BatchedFileRequests batchedFileRequest = new BatchedFileRequests();
                batchedFileRequest.addConatiner(fileImageContainer);
                batchedFileRequests.put(cacheKey, batchedFileRequest);

                FileImageRequest fileImageRequest = new FileImageRequest(filePath, Bitmap.Config.RGB_565, maxWidth, maxHeight, new FileResponse.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callError(error, cacheKey);
                    }
                }, new FileResponse.ReadListener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        callSuccess(response, cacheKey);
                    }
                });
                fileImageContainer.fileImageRequest = fileImageRequest;
                fileQueue.add(fileImageRequest);
            }
        }
        return fileImageContainer;
    }

    private void callSuccess(Bitmap bitmap, String cacheKey) {
        imageCache.putBitmap(cacheKey, bitmap);
        BatchedFileRequests batch = batchedFileRequests.get(cacheKey);
        batch.callSucess(bitmap, false);
        batchedFileRequests.remove(cacheKey);
    }

    private void callError(VolleyError volleyError, String cacheKey) {
        BatchedFileRequests batch = batchedFileRequests.get(cacheKey);
        batch.callError(volleyError);
        batchedFileRequests.remove(cacheKey);
    }

    /**
     * Creates a cache key for use with the L1 cache.
     * @param path The URL of the request.
     * @param maxWidth The max-width of the output.
     * @param maxHeight The max-height of the output.
     */
    private static String getCacheKey(String path, int maxWidth, int maxHeight) {
        return new StringBuilder(path == null ? 12 : path.length() + 12).append("#W").append(maxWidth)
                .append("#H").append(maxHeight).append(path).toString();
    }

    public static class FileImageContainer implements FileResponse.ReadListener<Bitmap>, FileResponse.ErrorListener {

        private FileImageContainer(String cacheKey, ImageLoader.ImageCache imageCache) {
            this.cacheKey = cacheKey;
            this.imageCache = imageCache;
        }

        private String cacheKey;
        private ImageLoader.ImageCache imageCache;

        private Listener listener;

        private FileImageRequest fileImageRequest;

        @Override
        public void onResponse(Bitmap response) {
            if(response != null) {
                imageCache.putBitmap(cacheKey, response);
                callListener(response, false);
            }
            else {
                callError(null);
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            callError(error);
        }

        private void callListener(Bitmap bitmap, boolean fromCache) {
            if(listener != null) {
                listener.onImageLoad(bitmap, fromCache);
            }
        }

        private void callError(VolleyError volleyError) {
            if(listener != null) {
                if(volleyError == null) {
                    volleyError = new VolleyError("Image loader error");
                }
                listener.onImageError(volleyError);
            }
        }

        public void cancel() {
            if(fileImageRequest != null) {
                fileImageRequest.cancel();
            }
        }
    }

    private class BatchedFileRequests {

        private LinkedList<FileImageContainer> requests = new LinkedList<>();

        public void addConatiner(FileImageContainer imageContainer) {
            requests.add(imageContainer);
        }

        public void callSucess(Bitmap bitmap, boolean fromCache) {
            for (Iterator<FileImageContainer> iterator = requests.iterator(); iterator.hasNext(); ) {
                FileImageContainer container = iterator.next();
                container.callListener(bitmap, fromCache);
                iterator.remove();
            }
        }

        public void callError(VolleyError volleyError) {
            for (Iterator<FileImageContainer> iterator = requests.iterator(); iterator.hasNext(); ) {
                FileImageContainer container = iterator.next();
                container.callError(volleyError);
                iterator.remove();
            }
        }

    }

    public interface Listener {
        void onImageLoad(Bitmap bitmap, boolean fromCache);
        void onImageError(VolleyError volleyError);
    }
}
