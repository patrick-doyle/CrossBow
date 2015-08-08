package com.crossbow.volley;

import com.android.volley.VolleyError;

/**

 */
public class FileError extends VolleyError {

    public FileError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public FileError(Throwable e) {
        super(e);
    }
}
