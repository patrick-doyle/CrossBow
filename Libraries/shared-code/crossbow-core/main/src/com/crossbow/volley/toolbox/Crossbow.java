package com.crossbow.volley.toolbox;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.AbsListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.BasicFileDelivery;
import com.crossbow.volley.CrossbowImage;
import com.crossbow.volley.CrossbowImageCache;
import com.crossbow.volley.FileDelivery;
import com.crossbow.volley.FileQueue;
import com.crossbow.volley.FileStack;

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

        FileDelivery fileDelivery = new BasicFileDelivery();
        FileStack fileStack = new BasicFileStack(context);
        fileQueue = new FileQueue(fileDelivery, fileStack);
        fileQueue.start();
        fileImageLoader = new FileImageLoader(fileQueue, imageCache);
    }

    /**
     * Creates an crossbow image request for easy image loading
     * @see CrossbowImage
     */
    public CrossbowImage.Builder loadImage() {
        return new CrossbowImage.Builder(context, imageLoader, fileImageLoader);
    }

    /**
     * Gets the backing RequestQueue
     */
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /**
     * Gets the backing CrossbowImageCache
     */
    public CrossbowImageCache getImageCache() {
        return imageCache;
    }

    /**
     * Gets the backing ImageLoader
     */
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     * Gets the backing FileImageLoader
     */
    public FileImageLoader getFileImageLoader() {
        return fileImageLoader;
    }

    /**
     * Add a request to the network queue.
     * @return the added request
     */
    public<T> Request<T> add(Request<T> request) {
        return requestQueue.add(request);
    }

    /**
     * Should be called in the {@link ComponentCallbacks2#onLowMemory() Context.onLowMemory()} to tell crossbow to trim caches and
     * clean up.
     */
    public void onLowMemory() {
        imageCache.onLowMemory();
    }

    /**
     * Should be called in the {@link ComponentCallbacks2#onTrimMemory(int)} () Context.onTrimMemory(int)} to tell crossbow to trim caches and
     * clean up.
     */
    public void onTrimMemory(int level) {
        imageCache.trimMemory(level);
    }

    /**
     * Cancels all requests in  request queue that have a certain tag
     */
    public void cancelAll(Object tag) {
        requestQueue.cancelAll(tag);
    }

    /**
     * Cancels all request in both the file and request queues
     */
    public void cancelAll() {
        requestQueue.cancelAll(REQUEST_FILTER_CANCEL_ALL);
    }

    /**
     * Cancels all requests in the request queue that match a certain filter
     */
    public void cancelAll(RequestQueue.RequestFilter requestFilter) {
        requestQueue.cancelAll(requestFilter);
    }


    /**
     * Listen to this list to pause the request queue when the list is flung
     * @param absListView the list to listen to.
     */
    public void listenToList(AbsListView absListView) {
        listenToList(absListView, null);
    }

    /**
     * Listen to this list to pause the request/file queue when the list is flung
     * @param absListView the list to listen to.
     * @param delegateListener optional listener to delegate the scroll events to
     */
    public void listenToList(AbsListView absListView, @Nullable AbsListView.OnScrollListener delegateListener) {
        if(absListView != null) {
            absListView.setOnScrollListener(new ScrollListener(delegateListener));
        }
    }

    private static final RequestQueue.RequestFilter REQUEST_FILTER_CANCEL_ALL = new RequestQueue.RequestFilter() {
        @Override
        public boolean apply(Request<?> request) {
            return true;
        }
    };

    private class ScrollListener implements AbsListView.OnScrollListener {

        private AbsListView.OnScrollListener delgateListener;

        public ScrollListener(AbsListView.OnScrollListener delgateListener) {

            this.delgateListener = delgateListener;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                Crossbow.this.requestQueue.start();
                Crossbow.this.fileQueue.start();
            }
            else if (scrollState == SCROLL_STATE_FLING) {
                Crossbow.this.requestQueue.stop();
                Crossbow.this.fileQueue.stop();
            }
            if(delgateListener != null) {
                delgateListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(delgateListener != null) {
                delgateListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    }
}
