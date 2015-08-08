package com.crossbow.volley.toolbox;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.CrossbowImage;
import com.crossbow.volley.CrossbowImageCache;
import com.crossbow.volley.FileQueue;
import com.crossbow.volley.FileRequest;
import com.crossbow.volley.FileRequestFilter;

public class Crossbow {
    private Context context;

    private RequestQueue requestQueue;

    private FileQueue fileQueue;

    private ImageLoader imageLoader;

    private FileImageLoader fileImageLoader;

    private CrossbowImageCache imageCache;

    private static Crossbow instance;

    /**
     * Creates a singleton stack that uses the default components.
     * @param context context of the application
     */
    public static Crossbow get(Context context) {
        if(instance == null) {
            initialize(context);
        }
        return instance;
    }

    /**
     * Called in the application class to pass in a different CrossbowComponents instance
     * @param context the application context
     * @param crossbowComponents interface for providing the parts needed for crossbow to work
     */
    public static void initialize(Context context, CrossbowComponents crossbowComponents) {
        instance = new Crossbow(context, crossbowComponents);
    }

    public static void initialize(Context context) {
        initialize(context, new DefaultCrossbowComponents(context));
    }

    public Crossbow(Context context, CrossbowComponents crossbowComponents) {
        this.context = context.getApplicationContext();
        this.requestQueue = crossbowComponents.provideRequestQueue();
        this.imageLoader = crossbowComponents.provideImageLoader();
        this.imageCache = crossbowComponents.provideImageCache();
        this.fileQueue = crossbowComponents.provideFileQueue();
        this.fileImageLoader = crossbowComponents.provideFileImageLoader();
    }

    public CrossbowImage.Builder loadImage() {
        return new CrossbowImage.Builder(context, imageLoader, fileImageLoader);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public CrossbowImageCache getImageCache() {
        return imageCache;
    }

    public FileQueue getFileQueue() {
        return fileQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public FileImageLoader getFileImageLoader() {
        return fileImageLoader;
    }

    public<T> Request<T> add(Request<T> request) {
        return requestQueue.add(request);
    }

    public<T> FileRequest<T> add(FileRequest<T> request) {
        fileQueue.add(request);
        return request;
    }

    public void cancelAll(Object tag) {
        requestQueue.cancelAll(tag);
        fileQueue.cancelAll(tag);
    }

    public void cancelAll() {
        requestQueue.cancelAll(requestFilter);
        fileQueue.cancelAll(fileRequestFilter);
    }

    public void cancelAll(RequestQueue.RequestFilter requestFilter) {
        requestQueue.cancelAll(requestFilter);
    }

    public void cancelAll(FileRequestFilter requestFilter) {
        fileQueue.cancelAll(requestFilter);
    }

    private static final RequestQueue.RequestFilter requestFilter = new RequestQueue.RequestFilter() {
        @Override
        public boolean apply(Request<?> request) {
            return true;
        }
    };

    private static final FileRequestFilter fileRequestFilter = new FileRequestFilter() {
        @Override
        public boolean apply(FileRequest<?> fileRequest) {
            return true;
        }
    };
}
