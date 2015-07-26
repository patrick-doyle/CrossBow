package com.crossbow.volley.toolbox;

import android.graphics.Bitmap;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.FileQueue;
import com.crossbow.volley.FileResponse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Patrick on 13/04/2015.
 */
public class FileImageLoader {

    private FileQueue fileQueue;

    private Map<String, BatchedFileRequests> batchedFileRequests = new HashMap<>();

    private ImageLoader.ImageCache imageCache;

    /**
     * Creates a new FileImageLoader
     * @param fileQueue the file queue to use to process the requests
     * @param imageCache the image cache to use. should be the same one used for the Network image loader to help prevent Out of Memory Errors
     */
    public FileImageLoader(FileQueue fileQueue, ImageLoader.ImageCache imageCache) {
        this.fileQueue = fileQueue;
        this.imageCache = imageCache;
    }

    /**
     * Creates a mew file image load. The {@link FileImageRequest} is used with the {@link ImageDecoder} to make the decodes run in time with the
     * crossbow network image loader. This will try to load from the sdcard first and then from assets. This will return the raw decoded image with no scaling
     * @param filePath the path to image
     * @param listener the listener to receive the image
     * @return A file image container. This is used to cancel the image load
     */
    public FileImageContainer get(String filePath, Listener listener) {
        return this.get(filePath, 0, 0, listener);
    }

    /**
     * Creates a mew file image load. The {@link FileImageRequest} is used with the {@link ImageDecoder} to make the decodes run in time with the
     * crossbow network image loader. This will try to load from the sdcard first and then from assets
     * @param filePath the path to image
     * @param maxWidth the max width of the image
     * @param maxHeight the max height to the image
     * @param listener the listener to receive the image
     * @return A file image container. This is used to cancel the image load
     */
    public FileImageContainer get(String filePath, int maxWidth, int maxHeight, Listener listener) {

        final String cacheKey = getCacheKey(filePath, maxWidth, maxHeight);

        FileImageContainer fileImageContainer = new FileImageContainer(listener);

        Bitmap bitmap = imageCache.getBitmap(cacheKey);
        if(bitmap != null) {
            fileImageContainer.callListener(bitmap, true);
        }
        else {

            //check if there is a request in flight
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
                }, new FileResponse.Listener<Bitmap>() {
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

    /**
     * Used to keep track of the requests as they go through the file queue. Also used to cancel the file request in flight if needed.
     */
    public static class FileImageContainer {

        private Listener listener;

        private FileImageRequest fileImageRequest;

        private FileImageContainer(Listener listener) {
            this.listener = listener;
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

    /**
     * Wrapper for handing a batch of requests for the same image size
     */
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
