package com.crossbow.volley;

import com.android.volley.VolleyError;

/**
 * Created by Patrick on 25/03/2015.
 */
public class FileResponse<T> {

    public T data;

    public VolleyError e;

    private FileResponse(T data) {
        this.data = data;
    }

    private FileResponse(VolleyError e) {
        this.e = e;
    }

    public boolean isError() {
        return e != null;
    }

    public static <T> FileResponse<T> error(Throwable e) {
        if(e == null) {
            return new FileResponse<>(new VolleyError());
        }
        return new FileResponse<>(new VolleyError(e));
    }

    public static <T> FileResponse<T> error() {
        return new FileResponse<>(new VolleyError());
    }

    public static <T> FileResponse<T> success(T data) {
        return new FileResponse<>(data);
    }

    public interface ErrorListener {
        void onErrorResponse(VolleyError error);
    }

    public interface Listener<T> {
        void onResponse(T response);
    }
}
