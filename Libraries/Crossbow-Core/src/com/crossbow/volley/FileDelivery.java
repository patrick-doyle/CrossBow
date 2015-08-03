package com.crossbow.volley;

import com.android.volley.VolleyError;

/**

 */
public interface FileDelivery {

    public void deliverSuccess(FileRequest<?> fileRequest, FileResponse<?> fileResponse);

    public void deliverError(FileRequest<?> fileRequest, VolleyError fileResponse);
}
