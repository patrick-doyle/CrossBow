Crossbow (In Development, not ready for use)
===========

Extension to the Volley library adding an easy to use wrapper around Volley that sets 
up the RequestQueue and Image cache for the ImageLoader.

Also provides an easy Picasso https://square.github.io/picasso/ inspired image loading
system based on the ImageLoader.

Crossbow does not modify the Volley library in any way so if you already use Volley then integrating
this library can replace it with no changes to your code.

Http Requests
------

Adding a request to the queue is easy and simple.
```java
StringRequest request = new StringRequest("http://www.url.com", new Response.Listener<String>() {
    @Override
    public void onResponse(String response) {
        //Handle response
    }
}, new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
        //Error handling
    }
});

Crossbow.queue(this,request);
```

If you need to get a reference to the RequestQueue then simply use 
```java
RequestQueue requestQueue = Crossbow.from(this).getRequestQueue();
```

Crossbow handles setting up the queue and managing it for you.

Image Loading
------

Crossbow also sets up an ImageLoader and a Memory Cache for the decoded and scaled bitmaps. Using this is very easy with the CrossbowImage class.

```java
CrossbowImage.from(context).url("https://i.imgur.com/5mObncZ.jpg").into(imageView);
```

The bitmaps are automatically scaled down to the size of the ImageView if the image is larger than the ImageView to reduce memory usage. 

CrossbowImage also supports placeholders
```java
CrossbowImage.from(context)
.defaultRes(R.drawable.placeHolder)
.error(R.drawable.errorImage)
.source("https://i.imgur.com/5mObncZ.jpg")
.into(imageView);
```

fading
```java
CrossbowImage.from(context)
.fade(200)
.source("https://i.imgur.com/5mObncZ.jpg")
.into(imageView);
```

and separate scaling for placeholders and error drawables.
```java
CrossbowImage.from(context)
.scale(ImageView.ScaleType.CENTER_CROP)
.placeholderScale(ImageView.ScaleType.CENTER)
.source("https://i.imgur.com/5mObncZ.jpg")
.into(imageView);
```

CrossbowImage can load from http/https network urls, file paths and drawable resource ids.

CrossbowImage will automatically detect ImageView reuse and cancel the old requests so it safe to use in adapters.

```java
@Override
public View getView(int position, View convertView, ViewGroup parent) {
    ImageView imageView = new ImageView(context);
    CrossbowImage.from(context).url("https://i.imgur.com/5mObncZ.jpg").into(imageView);
    return imageView;
}
```

If you need custom image loading/handing then you can use the standard Volley ImageLoader. This shares the same Threads, Cache and ImageLoader that CrossbowImage uses.
Getting a reference to the ImageLoader uses the same method as the RequestQueue.

```java
ImageLoader imageLoader = Crossbow.from(this).getImageLoader();

imageLoader.get("https://i.imgur.com/5mObncZ.jpg", new ImageLoader.ImageListener() {
    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }
});
```

File Loading
------
All file loading must be used with full paths

Crossbow supports background file operations with the FileRequest class and its subclasses. The creation and queuing
of requests is the same as the http requests. The order of loading is from the assets first and if the file is not found
it is attempted from the sdcard.

File requests can do any read/write operation on a file. FileRequests with the same path are executed in series but FileRequests
with different paths may be executed in parallel.

```java
FileImageRequest fileImageRequest = new FileImageRequest("/images/image.png", new FileResponse.Listener<Bitmap>() {
    @Override
    public void onResponse(Bitmap response) {
        //Handle response
    }
}, new FileResponse.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
        //Error handling
    }
});

Crossbow.queue(this, fileImageRequest);
```

The FileImageLoader is used in the same way as normal ImageLoader and can be used for custom file image loads
```java
FileImageLoader imageLoader = Crossbow.from(this).getFileImageLoader();

imageLoader.get("/images/image.png", new FileImageLoader.Listener() {
    @Override
    public void onResponse(Bitmap response, boolean isImmediate) {
        //Do somthing with the loaded file
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //Handle any errors here
    }
});
```

Using Custom Volley Components
------

If you want to change the way Volley works internally by using a custom Volley component such as the Network, HttpStack, ImageCache, ImageLoader, HttpCache or RequestQueue this easy to do with the VolleyComponents interface and your application class.

1. Create an application class and register it in the Manifest.

```java
public class App extends Application {

}
```

```xml
<application
    android:allowBackup="true"
    android:icon="@drawable/ic_launcher"
    android:label="@string/app_name"
    android:name=".App"
    android:theme="@style/AppTheme" >
    <activity
        android:name=".MyActivity"
        android:label="@string/app_name" >
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />

            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

2. Register the VolleyStack stack

```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Crossbow.registerStack(this, TestStack.class);
    }
}
```
Crossbow will automatically pick up on the new Stack and start using it when the app starts.

The stack can only have a single constructor with a Context passed in.

If you only want to replace one or two components of the VolleyStack you can extend the DefaultVolleyStack and implement the custom components you want to.

```java
public class CustomVolleyStack extends DefaultVolleyStack {
    
    public CustomVolleyStack(Context context) {
        super(context);
    }

    @Override
    public HttpStack getHttpStack() {
        CustomHttpStack stack = new CustomHttpStack();
        return stack;
    }
}
```
If you are extending the DefaultVolleyStack do not hold a reference to Context. Use the getContext() method instead.
