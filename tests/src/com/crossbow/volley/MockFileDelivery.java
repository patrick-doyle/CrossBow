package com.crossbow.volley;

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

    boolean shouldCallFinish = false;

    public MockFileDelivery(boolean shouldCallFinish) {
        this.shouldCallFinish = shouldCallFinish;
    }

    public MockFileDelivery() {
        this.shouldCallFinish = false;
    }

    @Override
    public void deliverSuccess(FileRequest<?> fileRequest, FileResponse<?> fileResponse) {
        FileResponse dummmy = fileResponse;
        fileRequest.preformDelivery(dummmy);
        if(shouldCallFinish) {
            fileRequest.finish();
        }
    }

    @Override
    public void deliverError(FileRequest<?> fileRequest, VolleyError fileResponse) {
        fileRequest.preformError(fileResponse);
        if(shouldCallFinish) {
            fileRequest.finish();
        }
    }
}
