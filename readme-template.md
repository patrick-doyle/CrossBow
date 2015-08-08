Crossbow
===========

Extension to the Volley library adding an easy to use wrapper around Volley. Supports android 2.3
and up on phones and 4.3 and up for the wear module

Also provides an easy Picasso https://square.github.io/picasso/ inspired image loading
system based on the ImageLoader.

Crossbow does not modify the Volley library in any way so if you already use Volley then
this library can replace it with no changes to your code.

Crossbow also uses OkHttp by default for Http/2 and SPDY support. You can provide a different OkHttp
client if you want.

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
    compile 'com.twistedequations.crossbow:crossbow:<<latest-version-here>>'
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

Crossbow.queue(this,request);
```

If you need to get a reference to the RequestQueue then simply use 
```java
RequestQueue requestQueue = Crossbow.from(this).getRequestQueue();
```

If you wish you can write a CustomRequest by extending thr Request class to handle any custom parsing,
headers etc that the library does not support directly.

GsonRequests
------

Crossbow has a GsonGetRequest/GsonPostRequest built in for easy fast parsing and mapping JSON to data
objects using the Gson library
```java
 GsonGetRequest<List<Repo>> requestRepos = new GsonGetRequest<List<Repo>>("https://api.github.com/users/twistedequations/repos", new Response.Listener<List<Repo>>() {
        @Override
        public void onResponse(List<Repo> response) {
        }
    }, new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    }){};
    Crossbow.get(this).add(requestRepos);
```
The GsonGetRequest/GsonPostRequest is an abstract class and must be used an anonymous subclass due to java's type erasure.
(Similar to the TypeToken used by Gson for Collections)

```java
GsonRequest<List<Repo>> requestRepos = new GsonRequest<List<Repo>>(params){};
```

You can use the GsonRequest class for custom gson requests when the GsonGetRequest/GsonPostRequest dont
suit your needs.

Image Loading
------
Crossbow also has rich image loading api based in volleys image loader.
Crossbow sets up an ImageLoader and a Memory Cache for the decoded and scaled bitmaps. Using this is
very easy using the Crossbow.loadImage().

```java
Crossbow.from(context).loadImage().source("https://i.imgur.com/5mObncZ.jpg").into(imageView).load();
```

The bitmaps are automatically scaled down to the size of the ImageView if the image is larger than
the ImageView to reduce memory usage and improve cache performance.

CrossbowImage also supports placeholders
```java
Crossbow.from(context)
    .loadImage()
    .placeHolder(R.drawable.placeHolder)
    .error(R.drawable.errorImage)
    .source("https://i.imgur.com/5mObncZ.jpg")
    .into(imageView);
```

fading
```java
Crossbow.from(context)
    .loadImage()
    .fade(200)
    .source("https://i.imgur.com/5mObncZ.jpg")
    .into(imageView);
```

and separate scaling for placeholders drawables, error drawables and the loaded image.
```java
Crossbow.from(context)
    .loadImage()
    .scale(ImageView.ScaleType.CENTER_CROP)
    .placeholderScale(ImageView.ScaleType.CENTER)
    .placeHolder(R.drawable.placeHolder)
    .source("https://i.imgur.com/5mObncZ.jpg")
    .into(imageView);
```

Crossbow can load from http/https network urls, File Uri, File paths and drawable resource ids.
```java
Crossbow.from(context)
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

File Loading
------
All file loading must be used with absolute paths

Crossbow supports background file operations with the FileRequest class and its subclasses. The creation and queuing
of requests is the same as the http requests. The file loader looks in the assets first and if the file is not found
it is attempted from the phone disk.

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
You can use the GsonReadFileRequest and GsonWriteFileRequest for easy reading and writing of json to files.

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

Crossbow Wear
===========

Crossbow Wear is exact same as Crossbow but built for use on wearable devices. It interconnects with
Crossbow on the phone and handles all the wear <-> handheld communication for you.

Setup
------

Add the CrossbowWear dependency to your build.gradle in your wear project
```groovy
dependencies {
    compile 'com.twistedequations.crossbow:crossbow-wear:<<latest-version-here>>'
}
```

On the Handheld app you need to have the main Crossbow and the Crossbow wear receiver libraries to
handle the in oncoming requests from the wearable
```groovy
dependencies {
 compile 'com.twistedequations.crossbow:crossbow:<<latest-version-here>>'
 compile 'com.twistedequations.crossbow:crossbow-wear-receiver:<<latest-version-here>>'
}
```

you also need to register the CrossbowListenerService in your manifest to listen for wear requests
```xml
<service android:name=".CrossbowListenerService">
    <intent-filter>
        <action android:name="com.google.android.gms.wearable.BIND_LISTENER" />
    </intent-filter>
</service>
```
If you already have a WearableListenerService set up for receiving events from a wearable you need to
change it to extend the CrossbowListenerService instead of the WearableListenerService and register it in instead.
```java
public class CustomWearService extends CrossbowListenerService {
}
```
```xml
<service android:name=".CustomWearService">
    <intent-filter>
        <action android:name="com.google.android.gms.wearable.BIND_LISTENER" />
    </intent-filter>
</service>
```
If you are using a custom crossbow instance or custom components you to need to let the CrossbowListenerService know
what RequestQueue to use for the wear requests. This is done is your subclass of CrossbowListenerService and overriding the
getRequestQueue() method.
```java
public class CustomWearService extends CrossbowListenerService {
    @Override
     public RequestQueue getRequestQueue() {
         return // Custom request queue here.
     }
 }
```
Lastly if you are using the compatibility api with custom compatibilities you need to add the crossbow wear
crossbow_compatibility string to the android_wear_capabilities string array so that the wearable knows
which node to connect to.
```xml
    <string-array name="android_wear_capabilities">
        <item>@string/crossbow_compatbility</item>
        <item>@string/other_compatibilty</item>
    </string-array>
```

Making Wear Requests
------

When on wear you need to use the CrossbowWear class to obtain a Crossbow instance. This
manages the caching, compression and play services communication for you via the play network class.
```java
Crossbow crossbow = CrossbowWear.get(this);
```
making a network request is the same as the handheld and can use all the same requests
```java
RepoNameRequest repoNameRequest = new RepoNameRequest("https://api.github.com/users/twistedequations/repos", new Response.Listener<List<String>>() {
        @Override
        public void onResponse(List<String> response) {
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    });
    CrossbowWear.get(this).add(repoNameRequest);
```

The Image Loaders work perfectly on Wear as well
```java
CrossbowWear.get(this)
    .loadImage()
    .centerCrop()
    .source("http://i.imgur.com/ByktT4N.jpg")
    .fade(200)
    .into(imageView)
    .load();
```

Response Transforming
------

Due to the wearable being a low power, having limited computational power and having a narrow
bandwidth to and from the wearable it makes sense to preform some of the parsing on the handheld to
shrink the network response.

First add the WearRequest interface to your request and return a unique key for the getTransFormerKey()
method an optional bundle of arguments for the transformer to use

```java
public class WearImageRequest extends RecycleImageRequest implements WearRequest {

    private final int maxWidth;
    private final int maxHeight;

    public WearImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
        super(url, listener, maxWidth, maxHeight, decodeConfig, errorListener);
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    public Bundle getTransformerParams() {
        Bundle bundle = new Bundle();
        bundle.putInt("maxWidth", maxWidth);
        bundle.putInt("maxHeight", maxHeight);
        return bundle;
    }

    @Override
    public String getTransFormerKey() {
        return "transformer_key";
    }
```

Next create the matching RequestTransformer on the handheld for the request to compress the data.
The Bundle passed in here will have the values as the Bundle from the WearRequest
getTransformerParams() method.

```java
public class ImageRequestTransformer implements ResponseTransformer {

    @Override
    public byte[] transform(Bundle requestArgs, byte[] data) throws ParseError {

        int width = requestArgs.getInt("width", 500);
        int height = requestArgs.getInt("height", 500);
        Bitmap.Config config = (Bitmap.Config) requestArgs.getSerializable("config");
        if(config == null) {
            config = Bitmap.Config.RGB_565;
        }

        Bitmap bitmap = ImageDecoder.parseImage(data, config, width, height);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream);
        return stream.toByteArray();
}
```

Lastly register the transformer and its key in a subclass of CrossbowListenerService using the same key
from the getTransFormerKey() method in the WearRequest
```java
public class WearMessageService extends CrossbowListenerService {

    @Override
    public Map<String, ResponseTransformer> getTransformerMap() {
        Map<String, ResponseTransformer> map = new HashMap<>();
        map.put("transformer_key", new CustomResponseTransformer());
        return map;
    }
}
```

}

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