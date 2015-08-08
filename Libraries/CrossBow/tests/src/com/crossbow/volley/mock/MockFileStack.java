package com.crossbow.volley.mock;

import com.android.volley.ParseError;
import com.android.volley.VolleyError;
import com.crossbow.volley.FileRequest;
import com.crossbow.volley.FileResponse;
import com.crossbow.volley.FileStack;

import java.io.IOException;

/**

 */
public class MockFileStack implements FileStack {

    private boolean shouldFail = false;

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public FileResponse performFileOperation(FileRequest fileRequest) throws VolleyError {
        if(shouldFail) {
            throw new ParseError(new IOException());
        }
        return FileResponse.success("mockdatarequest");
    }
}
