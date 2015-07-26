package com.crossbow.volley.toolbox;

import android.app.ActivityManager;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.crossbow.volley.CrossbowImage;
import com.crossbow.volley.CrossbowImageCache;
import com.crossbow.volley.FileQueue;
import com.crossbow.volley.FileRequest;
import com.crossbow.volley.FileRequestFilter;

import java.io.File;

public class Crossbow {

    private static final String TAG = "Crossbow";

    private Context context;

    private RequestQueue requestQueue;

    private FileQueue fileQueue;

    private ImageLoader imageLoader;

    private FileImageLoader fileImageLoader;

    private CrossbowImageCache imageCache;

    private static final int DISK_CACHE_SIZE = 15 * 1024 * 1024;

    private static Crossbow defaultInstance;

    /**
     * Creates a singleton stack that uses the default components. Use {@link com.crossbow.volley.toolbox.Crossbow.Builder Builder} to plugin custom components.
     * @param context context of the application
     */
    public static Crossbow get(Context context) {
        if(defaultInstance == null) {
            //Create a custom stack
            defaultInstance = new Crossbow.Builder(context).build();
        }
        return defaultInstance;
    }

    private Crossbow(Context context) {
        this.context = context.getApplicationContext();
    }

    public CrossbowImage.Builder loadImage() {
        return new CrossbowImage.Builder(imageLoader, fileImageLoader);
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

    /**
     * Used to create a new custom crossbow instance. Useful for swapping in custom components for custom behavior.
     * <br/>
     * Any custom crossbow instance should be wrapped ina singleton or stored in the application class.
     */
    public static class Builder {

        private Context context;

        private RequestQueue requestQueue;

        private ImageLoader imageLoader;

        private CrossbowImageCache imageCache;

        private FileQueue fileQueue;

        private FileImageLoader fileImageLoader;

        public Builder(Context context) {
            this.context = context.getApplicationContext();

            HttpStack httpStack = Default.defaultHttpStack();
            Cache cache = Default.defaultCache(context);
            Network network = Default.defaultNetwork(httpStack);

            imageCache = Default.defaultImageCache(context);
            requestQueue = Default.defaultQueue(cache, network);
            requestQueue.start();

            imageLoader = Default.defaultImageLoader(requestQueue, imageCache);

            fileQueue = Default.defaultFileQueue(context);
            fileQueue.start();
            fileImageLoader = Default.defaultFileLoader(fileQueue, imageCache);
        }

        public Builder setRequestQueue(RequestQueue requestQueue) {
            if(this.requestQueue != null) {
                this.requestQueue.stop();
            }
            this.requestQueue = requestQueue;
            return this;
        }

        public Builder setImageLoader(ImageLoader imageLoader) {
            this.imageLoader = imageLoader;
            return this;
        }

        public Builder setImageCache(CrossbowImageCache imageCache) {
            this.imageCache = imageCache;
            return this;
        }

        public Builder setfileQueue(FileQueue fileQueue) {
            this.fileQueue = fileQueue;
            return this;
        }

        public Builder setfileImageLoader(FileImageLoader fileImageLoader) {
            this.fileImageLoader = fileImageLoader;
            return this;
        }

        public Crossbow build() {
            Crossbow crossbow = new Crossbow(context);
            crossbow.requestQueue = requestQueue;
            crossbow.imageLoader = imageLoader;
            crossbow.imageCache = imageCache;
            crossbow.fileQueue = fileQueue;
            crossbow.fileImageLoader = fileImageLoader;
            return crossbow;
        }

        public static class Default {

            public static Cache defaultCache(Context context) {
                String cacheDir = context.getCacheDir() + File.separator + "CrossbowCache";
                File file = new File(cacheDir);
                return new DiskBasedCache(file, DISK_CACHE_SIZE);
            }

            public static Network defaultNetwork(HttpStack httpStack) {
                return new BasicNetwork(httpStack);
            }

            public static HttpStack defaultHttpStack() {
                String userAgent = System.getProperty("http.agent");

                if(userAgent == null) {
                    userAgent = "com/android/volley/0";
                }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    return new HurlStack();
                }
                else {
                    return new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
                }
            }

            public static RequestQueue defaultQueue(Cache cache, Network network) {
                return new RequestQueue(cache, network);
            }

            public static CrossbowImageCache defaultImageCache(Context context) {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                int memCacheSize = ((am.getMemoryClass() * 1024 * 1024) / 10);
                return new CrossbowImageCache(memCacheSize);
            }

            public static CrossbowImageLoader defaultImageLoader(RequestQueue requestQueue, CrossbowImageCache crossbowImageCache) {
                return new CrossbowImageLoader(requestQueue, crossbowImageCache);
            }

            public static FileImageLoader defaultFileLoader(FileQueue requestQueue, CrossbowImageCache crossbowImageCache) {
                return new FileImageLoader(requestQueue, crossbowImageCache);
            }

            public static FileQueue defaultFileQueue(Context context) {
                return new FileQueue(context);
            }
        }
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
