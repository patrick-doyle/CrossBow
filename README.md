Crossbow
===========
[ ![Download](https://api.bintray.com/packages/twistedequations/maven/crossbow-wear-receiver/images/download.svg) ](https://bintray.com/twistedequations/maven/crossbow-wear-receiver/_latestVersion)

Contents
--------
[ Changelog](readme/changes.md)  
[ Crossbow Wear](readme/crossbow-wear.md)  
[ Crossbow Gson](readme/gson-requests.md)  

Current Version Number - 0.8.9.5

All Crossbow Libraries

######Base Crossbow Library
```groovy
   compile 'com.twistedequations.crossbow:crossbow:0.8.9.5'
```

######Crossbow Wear
```groovy
   compile 'com.twistedequations.crossbow:crossbow-wear:0.8.9.5'
```

######Crossbow Wear Receiver
```groovy
   compile 'com.twistedequations.crossbow:crossbow-wear-receiver:0.8.9.5'
```

######Crossbow Gson
```groovy
   compile 'com.twistedequations.crossbow:crossbow-gson:0.8.9.5'
```

Extension to the Volley library adding an easy to use wrapper around Volley. Supports android 2.3
and up on phones and 4.3 and up for the wear module

Also provides an easy Picasso https://square.github.io/picasso/ inspired image loading
system based on the ImageLoader.

Crossbow does not modify the Volley library in any way so if you already use Volley then
this library can replace it with no changes to your code.

Crossbow will use OkHttp if it finds it in the project. You can force the use a custom OkHttpClient
by using a custom CrossbowComponents or by extending the DefaultCrossbowComponents.

Getting Started
------
Crossbow is hosted on jcenter
```groovy
buildscript {
    repositories {
        jcenter()
    }
}
```
Add the Crossbow dependency to your build.gradle in your wear project
```groovy
dependencies {
    compile 'com.twistedequations.crossbow:crossbow:0.8.9.5'
}
```

Crossbow does all the heavy setup lifting for you including setting up the Request
Queue, Image Caches, Image Loaders and Http Clients.

A default Crossbow singleton can be obtained using

```java
CrossBow.get(Context context);
```

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

Crossbow.get(context).async(request);
```

This will execute the request asynchronously and the callbacks will be invoked when the request has finished on the main thread.

If you want to execute a request synchronously then use the sync method. The callbacks will not be invoked and it is safe to pass
null for the callbacks. 

```java
GsonGetRequest<List<Repo>> requestRepos = new  GsonGetRequest<List<Repo>>("https://api.github.com/users/twistedequations/repos", null, null){};
SyncResponse<List<Repo>> response = Crossbow.get(context).sync(requestRepos);
```

If you need to get a reference to the RequestQueue then simply use 
```java
RequestQueue requestQueue = Crossbow.get(context).getRequestQueue();
```

If you wish you can write a CustomRequest by extending thr Request class to handle any custom parsing,
headers etc that the library does not support directly.

Image Loading
------
Crossbow also has rich image loading api based in volleys image loader.
Crossbow sets up an ImageLoader and a Memory Cache for the decoded and scaled bitmaps. Using this is
very easy using the Crossbow.loadImage().

```java
Crossbow.get(context).loadImage().source("https://i.imgur.com/5mObncZ.jpg").into(imageView).load();
```

The bitmaps are automatically scaled down to the size of the ImageView if the image is larger than
the ImageView to reduce memory usage and improve cache performance.

CrossbowImage also supports placeholders
```java
Crossbow.get(context)
    .loadImage()
    .placeHolder(R.drawable.placeHolder)
    .error(R.drawable.errorImage)
    .source("https://i.imgur.com/5mObncZ.jpg")
    .into(imageView);
```

fading
```java
Crossbow.get(context)
    .loadImage()
    .fade(200)
    .source("https://i.imgur.com/5mObncZ.jpg")
    .into(imageView);
```

and separate scaling for placeholders drawables, error drawables and the loaded image.
```java
Crossbow.get(context)
    .loadImage()
    .scale(ImageView.ScaleType.CENTER_CROP)
    .placeholderScale(ImageView.ScaleType.CENTER)
    .placeHolder(R.drawable.placeHolder)
    .source("https://i.imgur.com/5mObncZ.jpg")
    .into(imageView);
```

Crossbow can load from http/https network urls, File Uri, File paths and drawable resource ids.
```java
Crossbow.get(context)
    .loadImage()
    .scale(ImageView.ScaleType.CENTER_CROP)
    .placeholderScale(ImageView.ScaleType.CENTER)
    .placeHolder(R.drawable.placeHolder)
    .source("/path/to/file/image.png")
    .into(imageView);
```

Crossbow will automatically detect ImageView reuse and cancel the old requests so it safe to
use in adapters.

```java
@Override
public View getView(int position, View convertView, ViewGroup parent) {
    ImageView imageView = new ImageView(context);
    Crossbow.from(context).loadImage().url("https://i.imgur.com/5mObncZ.jpg").into(imageView);
    return imageView;
}
```

If you need custom network image loading/handing then you can use the standard Volley ImageLoader. This
shares the same Threads, Cache and ImageLoader that CrossbowImage uses.
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

File image loading is done with the FileImageLoader

```java
FileImageLaoder imageLoader = Crossbow.from(this).getImageLoader();

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

If you want to use your own components you can implement CrossbowComponents or its simpler subclass
DefaultCrossbowComponents which has hooks to override the default components. If you wanted to use a
custom network of a different OkHttpClient

```java
public class CustomCrossbowBuilder extends DefaultCrossbowComponents {

    @Override
    public Network onCreateNetwork(HttpStack httpStack) {
        return new CustomNetwork(httpStack);
    }

    @Override
    public OkHttpClient onCreateHttpClient() {
        return new CustomOkHttpClient();
    }
}
```
And register it using in the application class
```java
Crossbow.initialize(context, components);
```
or any where before calling
```java
Crossbow.get(context);
```
for the first time.

If you want to manage the crossbow singleton yourself you can use
```java
Crossbow crossbow = new Crossbow(crossbow, components);
```
and store it manually in the application for a singleton wrapper.

Note:
If you use dagger2 you can extend the CrossbowComponents interface and annotate it with a module to
use dependency injection

## License

```
Copyright 2015 Patrick Doyle

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
