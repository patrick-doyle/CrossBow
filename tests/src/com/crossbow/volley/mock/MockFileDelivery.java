package com.crossbow.volley.mock;

import com.android.volley.VolleyError;
import com.crossbow.volley.FileDelivery;
import com.crossbow.volley.FileRequest;
import com.crossbow.volley.FileResponse;

/**
 * Created by Patrick on 29/03/2015.
 *
 * Posts the reslts of the background thread of the dispactcher
 */
public class MockFileDelivery implements FileDelivery {

    @Override
    public void deliverSuccess(FileRequest<?> fileRequest, FileResponse<?> fileResponse) {
        FileResponse dummmy = fileResponse;
        fileRequest.preformDelivery(dummmy);
    }

    @Override
    public void deliverError(FileRequest<?> fileRequest, VolleyError fileResponse) {
        fileRequest.preformError(fileResponse);

    }
}
