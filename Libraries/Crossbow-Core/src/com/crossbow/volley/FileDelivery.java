package com.crossbow.volley;

import com.android.volley.VolleyError;

/**
 * Created by Patrick on 29/03/2015.
 */
public interface FileDelivery {

    public void deliverSuccess(FileRequest<?> fileRequest, FileResponse<?> fileResponse);

    public void deliverError(FileRequest<?> fileRequest, VolleyError fileResponse);
}
