Crossbow
===========

Extension to the Volley library adding an easy to use wrapper around Volley that sets 
up the RequestQueue and Image cache for the ImageLoader.

Also provides an easy Picasso https://square.github.io/picasso/ inspired image loading
system based on the ImageLoader.

Crossbow does not modify the Volley library in any way so if you already use Volley then integrating this into your project should be easy.

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

Crossbow.from(this).addRequest(request);
```

If you need to get a reference to the RequestQueue then simply use 
```java
RequestQueue requestQueue = Crossbow.from(this).getRequestQueue();
```

TwistVolley handles setting up the queue and managing it for you.

Image Loading
------

Crossbow also sets up an Imageloader and a Memory Cache for the decoded and scaled bitmaps. Using this is very easy with the CrossbowImage class.

```java
CrossbowImage.from(context).url("https://i.imgur.com/5mObncZ.jpg").into(imageView);
```

The bitmaps are automatically scaled down to the size of the ImageView if the image is larger than the ImageView to reduce memory usage. 

CrossbowImage also supports placeholders,

```java
CrossbowImage.from(context)
.defaultRes(R.drawable.placeHolder)
.error(R.drawable.errorImage)
.url("https://i.imgur.com/5mObncZ.jpg")
.into(imageView);
```
fading 

```java
CrossbowImage.from(context)
.fade(200)
.url("https://i.imgur.com/5mObncZ.jpg")
.into(imageView);
```
and Scaling.

```java
CrossbowImage.from(context)
.scale(ImageView.ScaleType.CENTER_CROP)
.url("https://i.imgur.com/5mObncZ.jpg")
.into(imageView);
```

CrossbowImage will automatically detect ImageView reuse and cancel the old requests so it safe to use in adapters.

```java
@Override
public View getView(int position, View convertView, ViewGroup parent) {
    ImageView imageView = new ImageView(context);
    CrossbowImage.from(context).url("https://i.imgur.com/5mObncZ.jpg").into(imageView);
    return imageView;
}
```

If you need custom image loading/handing then you can use the standard Volley ImageLoader. This shares the same Threads, Cache and ImageLoader that TwistImage uses.
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
