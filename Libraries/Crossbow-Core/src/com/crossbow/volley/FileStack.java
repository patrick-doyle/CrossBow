package com.crossbow.volley;

import com.android.volley.VolleyError;

/**
 * Created by Patrick on 24/03/2015.
 *
 * Opens the file for working on. Also wraps the files exceptions into VolleyErrors for processing by the file
 *
 */
public interface FileStack {

    FileResponse performFileOperation(FileRequest fileRequest) throws VolleyError;

}
