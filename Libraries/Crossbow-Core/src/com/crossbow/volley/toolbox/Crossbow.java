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

    private static final String TAG = "Crossbow";

    private Context context;

    private RequestQueue requestQueue;

    private FileQueue fileQueue;

    private ImageLoader imageLoader;

    private FileImageLoader fileImageLoader;

    private CrossbowImageCache imageCache;

    private static Crossbow defaultInstance;

    /**
     * Creates a singleton stack that uses the default components.
     * @param context context of the application
     */
    public static Crossbow get(Context context) {
        return get(context, new CrossbowBuilder(context));
    }

    /**
     * Creates a singleton that uses the components from the CrossbowBuilder. Use {@link com.crossbow.volley.toolbox.CrossbowBuilder Builder} to plugin custom components.
     * @param context context of the application
     */
    public static Crossbow get(Context context, CrossbowBuilder crossbowBuilder) {
        if(defaultInstance == null) {
            //Create a custom stack
            defaultInstance = new Crossbow(context, crossbowBuilder);
        }
        return defaultInstance;
    }

    public Crossbow(Context context, CrossbowBuilder crossbowBuilder) {
        this.context = context.getApplicationContext();
        this.requestQueue = crossbowBuilder.requestQueue;
        this.imageLoader = crossbowBuilder.imageLoader;
        this.imageCache = crossbowBuilder.imageCache;
        this.fileQueue = crossbowBuilder.fileQueue;
        this.fileImageLoader = crossbowBuilder.fileImageLoader;
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
