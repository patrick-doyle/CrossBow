TwistVolley
===========

Extension to the Volley library adding an easy to use wrapper around volley that sets 
up the RequestQueue and Image cache for the ImageLoader.

Also provides an easy Picasso inspired image loading
system based on ImageLoader.

TwistVolley does not modify the voley library in any way so If you already use Volley then integrating this into your project should be easy.

Adding a request to the queue is easy and simple.
```StringRequest request = new StringRequest("http://www.url.com", new Response.Listener<String>() {
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
```

TwistVolley.from(this).addRequest(request);
