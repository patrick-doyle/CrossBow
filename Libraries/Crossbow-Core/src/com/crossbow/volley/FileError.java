package com.crossbow.volley;

import com.android.volley.VolleyError;

/**
 * Created by Patrick on 24/03/2015.
 */
public class FileError extends VolleyError {

    public FileError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public FileError(Exception e) {
        super(e);
    }
}
