GsonRequests
------

To use the gson requests you need to add the dependency to your project which contains the requests 
 
```groovy
dependencies {
    compile 'com.twistedequations.crossbow:crossbow:<<latest-version-here>>'
    compile 'com.twistedequations.crossbow:crossbow-gson:<<latest-version-here>>'
}
```
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
    Crossbow.get(context).add(requestRepos);
```
The GsonGetRequest/GsonPostRequest is an abstract class and must be used an anonymous subclass due to java's type erasure.
(Similar to the TypeToken used by Gson for Collections)

```java
GsonRequest<List<Repo>> requestRepos = new GsonRequest<List<Repo>>(params){};
```

You can use the GsonRequest class for custom gson requests when the GsonGetRequest/GsonPostRequest wont
suit your needs.

If you need to pas a custom gson for parsing you can pass it as constructor argument
```java
 GsonGetRequest<List<Repo>> requestRepos = new GsonGetRequest<List<Repo>>("https://api.github.com/users/twistedequations/repos", new Gson(), new Response.Listener<List<Repo>>() {
        @Override
        public void onResponse(List<Repo> response) {
        }
    }, new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    }){};
    Crossbow.get(context).add(requestRepos);
```