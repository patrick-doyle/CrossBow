TwistVolley
===========

Extension to the Volley library adding an easy to use wrapper around volley that sets 
up the RequestQueue and Image cache for the ImageLoader.

Also provides an easy Picasso https://square.github.io/picasso/ inspired image loading
system based on the ImageLoader.

TwistVolley does not modify the voley library in any way so If you already use Volley then integrating this into your project should be easy.

Http Requests
------

Adding a request to the queue is easy and simple.
```
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

TwistVolley.from(this).addRequest(request);
```

If you need to get a reference to the RequestQueue then simply use 
```
RequestQueue requestQueue = TwistVolley.from(this).getRequestQueue();
```

TwistVolley handles setting up the queue and managing itfor you.

Image Loading
------

TwistVolley also sets up an Imageloader and an Memory Cache for the decoded and scaled bitmaps. Using this is very easy with the TwistImage class.

```
TwistImage.from(context).url("https://i.imgur.com/5mObncZ.jpg").into(imageView);
```

The bitmaps are automaticly scaled down to the size of the ImageView if the image is larger than the ImageView to reduce memory usage. 

TwistImage also suports placeholders,

```
TwistImage.from(context)
.defaultRes(R.drawable.placeHolder)
.error(R.drawable.errorImage)
.url("https://i.imgur.com/5mObncZ.jpg")
.into(imageView);
```
fading 

```
TwistImage.from(context)
.fade(200)
.url("https://i.imgur.com/5mObncZ.jpg")
.into(imageView);
```
and Scaling.

```
TwistImage.from(context)
.scale(ImageView.ScaleType.CENTER_CROP)
.url("https://i.imgur.com/5mObncZ.jpg")
.into(imageView);
```

TwistImage will automatically detect ImageView reuse and cancel the old requests so it safe to use in adapters.

```
@Override
public View getView(int position, View convertView, ViewGroup parent) {
    ImageView imageView = new ImageView(context);
    TwistImage.from(context).url("https://i.imgur.com/5mObncZ.jpg").into(imageView);
    return imageView;
}
```

If you need custom image loading/handing then you can use the standard Volley ImageLoader. This shares the same Threads,Cache and ImageLoader that TwistImage uses.
Getting a refernece to the ImageLoader uses the same method as the RequestQueue.
```
ImageLoader imageLoader = TwistVolley.from(this).getImageLoader();
    imageLoader.get("https://i.imgur.com/5mObncZ.jpg", new ImageLoader.ImageListener() {
        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
            
        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });
```
