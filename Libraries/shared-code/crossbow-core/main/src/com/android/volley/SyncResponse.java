package com.android.volley;

public class SyncResponse<T> {

    public final T data;

    public final VolleyError volleyError;

    public SyncResponse(T data, VolleyError volleyError) {
        this.data = data;
        this.volleyError = volleyError;
    }

    public SyncResponse(T data) {
        this(data, null);
    }

    public SyncResponse(VolleyError volleyError) {
        this(null, volleyError);
    }

    public boolean isSuccess() {
        return volleyError == null && data != null;
    }

}
