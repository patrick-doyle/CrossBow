package com.crossbow.volley;

import com.android.volley.VolleyError;

/**
 * Created by Patrick on 25/03/2015.
 */
public class FileResponse<T> {

    public T data;

    public FileResponse(T data) {
        this.data = data;
    }

    public interface ErrorListener {
        void onErrorResponse(VolleyError error);
    }

    public interface ReadListener<T> {
        void onResponse(T response);
    }

    public interface WriteListener {
        void onResponse();
    }
}
