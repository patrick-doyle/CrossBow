package com.twist.volley.toolbox;

import android.content.Context;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.twist.volley.VolleyStack;


/**
 * Created by Patrick on 11/01/14.
 */
public class TwistVolley {

    private Context context;
    private static VolleyStack volleyStack;

    private static RequestQueue requestQueue;
    private static ImageLoader imageLoader;
    private static ImageLoader.ImageCache imageCache;

    public TwistVolley(Context context) {
        this.context = context.getApplicationContext();
        if(volleyStack == null) {
            volleyStack = onCreateVolleyStack();
        }
        buildFromStack(volleyStack);
    }

    protected final Context getContext() {
        return context.getApplicationContext();
    }

    protected VolleyStack onCreateVolleyStack() {
        return new DefaultVolleyStack(getContext());
    }

    private void buildFromStack(VolleyStack volleyStack) {
        if(requestQueue == null) {
            requestQueue = volleyStack.createRequestQueue();
        }

        if(imageCache == null) {
            imageCache = volleyStack.createImageCache();
        }

        if(imageLoader == null) {
            imageLoader = volleyStack.createImageLoader(requestQueue, imageCache);
        }
    }

    public final RequestQueue getRequestQueue(){
        return requestQueue;
    }

    /**
     * Adds a request to the shared queue
     */
    public final <T> void addToQueue(Request<T> request) {
        requestQueue.add(request);
    }

    /**
     * Stop the shared queue
     */
    public final void stopQueue() {
       requestQueue.stop();
    }

    /**
     * Start the shared queue
     */
    public final void startQueue() {
        requestQueue.start();
    }

    /**
     * Gets the shared image loader instance
     */
    public final ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     * Gets the {@link com.android.volley.toolbox.ImageLoader.ImageCache} used in the image loader.
     */
    public final ImageLoader.ImageCache getImageCache(){
        return imageCache;
    }

    /**
     * Test if the app is running on something newer than HoneyComb (API >= 11)
     */
    public static boolean isHoneyCombOrNewer(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);
    }

    /**
     * Cancels all requests in the queue.
     * <p>Throws Runtime exception if the application object does not extend RWVolleyApplication</p>
     *
     */
    public void cancelAll() {
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }
}
