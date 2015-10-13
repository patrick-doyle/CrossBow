package com.crossbow.volley;

import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.crossbow.volley.toolbox.OkHttpStack;
import com.squareup.okhttp.OkHttpClient;

public class HttpStackSelector {

    private static String USER_AGENT = "crossbow/0";

    public static HttpStack createStack() {
        if(hasOkHttp()) {
            OkHttpClient okHttpClient = new OkHttpClient();
            VolleyLog.d("OkHttp found, using okhttp for http stack");
            return new OkHttpStack(okHttpClient);
        }
        else if (useHttpClient()){
            VolleyLog.d("Android version is older than Gingerbread (API 9), using HttpClient");
            return new HttpClientStack(AndroidHttpClient.newInstance(USER_AGENT));
        }
        else {
            VolleyLog.d("Using Default HttpUrlConnection");
            return new HurlStack();
        }
    }

    private static boolean hasOkHttp() {
        try {
            Class.forName( "com.squareup.okhttp.OkHttpClient" );
            return true;
        } catch( ClassNotFoundException e ) {
            return false;
        }
    }

    private static boolean useHttpClient() {
        return Build.VERSION.SDK_INT < 9;
    }
}
